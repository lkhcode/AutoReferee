/*
 * Copyright (c) 2009 - 2025, DHBW Mannheim - TIGERs Mannheim
 */

package edu.tigers.sumatra;

import edu.tigers.sumatra.views.SumatraView;
import edu.tigers.sumatra.views.ESumatraViewType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.infonode.docking.View;
import net.infonode.docking.ViewSerializer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Optional;


/**
 * Enhanced window view serializer with improved error handling and logging.
 * This class handles the serialization and deserialization of window views
 * for the docking framework, providing better user feedback and error recovery.
 */
@Log4j2
@RequiredArgsConstructor
class WindowViewSerializer implements ViewSerializer
{
	private final Collection<SumatraView> views;


	@Override
	public void writeView(final View view, final ObjectOutputStream out) throws IOException
	{
		if (view == null) 
		{
			log.warn("Attempted to serialize null view");
			throw new IOException("Cannot serialize null view");
		}
		
		String title = view.getTitle();
		if (title == null || title.trim().isEmpty()) 
		{
			log.warn("View has null or empty title, cannot serialize");
			throw new IOException("View title cannot be null or empty");
		}
		
		Optional<ESumatraViewType> matchingType = findViewTypeByTitle(title);
		if (matchingType.isPresent())
		{
			int viewId = matchingType.get().getId();
			out.writeInt(viewId);
			log.trace("Successfully serialized view '{}' with ID {}", title, viewId);
		}
		else
		{
			log.error("Unknown view type for title: '{}'. Available types: {}", 
					title, 
					java.util.Arrays.stream(ESumatraViewType.values())
						.map(ESumatraViewType::getTitle)
						.collect(java.util.stream.Collectors.joining(", ")));
			throw new IOException("Unknown view type: " + title);
		}
	}
	
	/**
	 * Finds a view type by its title with improved matching logic
	 */
	private Optional<ESumatraViewType> findViewTypeByTitle(String title)
	{
		return java.util.Arrays.stream(ESumatraViewType.values())
				.filter(viewType -> viewType.getTitle().equals(title))
				.findFirst();
	}


	@Override
	public View readView(final ObjectInputStream in) throws IOException
	{
		int id = in.readInt();
		log.trace("Attempting to deserialize view with ID: {}", id);

		// Try to find the view in our current collection
		Optional<SumatraView> matchingView = findViewById(id);
		if (matchingView.isPresent())
		{
			SumatraView view = matchingView.get();
			log.trace("Successfully deserialized view '{}' with ID {}", 
					view.getType().getTitle(), id);
			return view.getView();
		}

		// If not found, check if it's a known type that's been removed
		ESumatraViewType type = ESumatraViewType.fromId(id);
		if (type != null)
		{
			log.warn("View '{}' (ID: {}) exists in enum but is not available in current view collection. " +
					"This view may have been temporarily disabled or removed from this configuration.", 
					type.getTitle(), id);
		}
		else
		{
			log.error("Unknown view ID {} encountered during deserialization. " +
					"This may indicate a version mismatch or corrupted layout file. " +
					"Available view IDs: {}", 
					id, 
					views.stream()
						.map(v -> v.getType().getId())
						.map(String::valueOf)
						.collect(java.util.stream.Collectors.joining(", ")));
		}
		
		return null; // Return null to skip this view in the layout
	}
	
	/**
	 * Finds a view by its ID with improved error handling
	 */
	private Optional<SumatraView> findViewById(int id)
	{
		return views.stream()
				.filter(view -> view.getType().getId() == id)
				.findFirst();
	}
}
