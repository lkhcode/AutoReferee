/*
 * Copyright (c) 2009 - 2025, DHBW Mannheim - TIGERs Mannheim
 */

package edu.tigers.sumatra.ui;

import edu.tigers.sumatra.views.ESumatraViewType;
import edu.tigers.sumatra.views.SumatraView;
import lombok.extern.log4j.Log4j2;
import net.infonode.docking.View;

import javax.swing.SwingUtilities;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enhanced view manager that provides better tracking and management of UI views.
 * Includes performance monitoring, error handling, and user feedback.
 */
@Log4j2
public class EnhancedViewManager
{
	private final Map<ESumatraViewType, SumatraView> viewRegistry = new ConcurrentHashMap<>();
	private final Map<ESumatraViewType, Long> viewAccessTimes = new ConcurrentHashMap<>();
	private final Map<ESumatraViewType, Integer> viewAccessCounts = new ConcurrentHashMap<>();
	private EnhancedStatusDisplay statusDisplay;
	
	public EnhancedViewManager(Collection<SumatraView> views)
	{
		registerViews(views);
	}
	
	/**
	 * Sets the status display for user feedback
	 */
	public void setStatusDisplay(EnhancedStatusDisplay statusDisplay)
	{
		this.statusDisplay = statusDisplay;
	}
	
	/**
	 * Registers views in the manager
	 */
	private void registerViews(Collection<SumatraView> views)
	{
		viewRegistry.clear();
		viewAccessTimes.clear();
		viewAccessCounts.clear();
		
		for (SumatraView view : views)
		{
			viewRegistry.put(view.getType(), view);
			viewAccessTimes.put(view.getType(), System.currentTimeMillis());
			viewAccessCounts.put(view.getType(), 0);
		}
		
		log.info("Registered {} views in enhanced view manager", views.size());
	}
	
	/**
	 * Gets a view by type with usage tracking
	 */
	public Optional<SumatraView> getView(ESumatraViewType viewType)
	{
		SumatraView view = viewRegistry.get(viewType);
		if (view != null)
		{
			// Track usage
			viewAccessTimes.put(viewType, System.currentTimeMillis());
			viewAccessCounts.merge(viewType, 1, Integer::sum);
			
			log.trace("View '{}' accessed (total: {} times)", viewType.getTitle(), 
					viewAccessCounts.get(viewType));
			
			return Optional.of(view);
		}
		
		log.warn("View type '{}' not found in registry", viewType);
		return Optional.empty();
	}
	
	/**
	 * Shows a view with enhanced error handling and user feedback
	 */
	public void showView(ESumatraViewType viewType)
	{
		SwingUtilities.invokeLater(() -> {
			try
			{
				Optional<SumatraView> viewOpt = getView(viewType);
				if (viewOpt.isEmpty())
				{
					showError("View '" + viewType.getTitle() + "' is not available");
					return;
				}
				
				SumatraView view = viewOpt.get();
				view.ensureInitialized();
				
				View dockingView = view.getView();
				if (dockingView.getRootWindow() == null)
				{
					// View is not currently shown, restore it
					dockingView.restoreFocus();
					showSuccess("Opened " + viewType.getTitle());
				}
				else
				{
					// View is already shown, just focus it
					dockingView.restoreFocus();
					showSuccess("Focused " + viewType.getTitle());
				}
			}
			catch (Exception e)
			{
				log.error("Failed to show view: {}", viewType.getTitle(), e);
				showError("Failed to open " + viewType.getTitle() + ": " + e.getMessage());
			}
		});
	}
	
	/**
	 * Hides a view with user feedback
	 */
	public void hideView(ESumatraViewType viewType)
	{
		SwingUtilities.invokeLater(() -> {
			try
			{
				Optional<SumatraView> viewOpt = getView(viewType);
				if (viewOpt.isEmpty())
				{
					showWarning("View '" + viewType.getTitle() + "' is not available");
					return;
				}
				
				SumatraView view = viewOpt.get();
				View dockingView = view.getView();
				
				if (dockingView.getRootWindow() != null)
				{
					dockingView.close();
					showSuccess("Closed " + viewType.getTitle());
				}
				else
				{
					showWarning(viewType.getTitle() + " is already closed");
				}
			}
			catch (Exception e)
			{
				log.error("Failed to hide view: {}", viewType.getTitle(), e);
				showError("Failed to close " + viewType.getTitle() + ": " + e.getMessage());
			}
		});
	}
	
	/**
	 * Gets view usage statistics
	 */
	public Map<ESumatraViewType, ViewUsageStats> getUsageStatistics()
	{
		Map<ESumatraViewType, ViewUsageStats> stats = new HashMap<>();
		
		for (ESumatraViewType viewType : viewRegistry.keySet())
		{
			stats.put(viewType, new ViewUsageStats(
				viewAccessCounts.getOrDefault(viewType, 0),
				viewAccessTimes.getOrDefault(viewType, 0L)
			));
		}
		
		return stats;
	}
	
	/**
	 * Gets the most frequently used views
	 */
	public java.util.List<ESumatraViewType> getMostUsedViews(int limit)
	{
		return viewAccessCounts.entrySet().stream()
			.sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
			.limit(limit)
			.map(Map.Entry::getKey)
			.collect(java.util.stream.Collectors.toList());
	}
	
	/**
	 * Resets usage statistics
	 */
	public void resetStatistics()
	{
		viewAccessCounts.clear();
		viewAccessTimes.clear();
		log.info("View usage statistics reset");
		showSuccess("Usage statistics reset");
	}
	
	private void showSuccess(String message)
	{
		if (statusDisplay != null)
		{
			statusDisplay.showSuccess(message);
		}
	}
	
	private void showWarning(String message)
	{
		if (statusDisplay != null)
		{
			statusDisplay.showWarning(message);
		}
	}
	
	private void showError(String message)
	{
		if (statusDisplay != null)
		{
			statusDisplay.showError(message);
		}
	}
	
	/**
	 * View usage statistics data class
	 */
	public static class ViewUsageStats
	{
		public final int accessCount;
		public final long lastAccessTime;
		
		public ViewUsageStats(int accessCount, long lastAccessTime)
		{
			this.accessCount = accessCount;
			this.lastAccessTime = lastAccessTime;
		}
		
		public boolean isRecentlyUsed(long thresholdMs)
		{
			return System.currentTimeMillis() - lastAccessTime < thresholdMs;
		}
		
		@Override
		public String toString()
		{
			return String.format("ViewUsageStats{count=%d, lastAccess=%d}", 
				accessCount, lastAccessTime);
		}
	}
}