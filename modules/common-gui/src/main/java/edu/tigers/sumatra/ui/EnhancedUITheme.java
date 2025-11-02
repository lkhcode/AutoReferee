/*
 * Copyright (c) 2009 - 2025, DHBW Mannheim - TIGERs Mannheim
 */

package edu.tigers.sumatra.ui;

import lombok.extern.log4j.Log4j2;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.gui.colorprovider.ColorProvider;
import net.infonode.gui.colorprovider.FixedColorProvider;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Font;

/**
 * Enhanced UI theme provider for Sumatra application.
 * Provides modern styling with improved colors, fonts, and visual effects.
 */
@Log4j2
public class EnhancedUITheme
{
	// Modern color palette
	private static final Color BACKGROUND_PRIMARY = new Color(250, 250, 252);
	private static final Color BACKGROUND_SECONDARY = new Color(245, 245, 247);
	private static final Color ACCENT_COLOR = new Color(59, 130, 246);
	private static final Color ACCENT_HOVER = new Color(37, 99, 235);
	private static final Color BORDER_COLOR = new Color(229, 231, 235);
	private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
	private static final Color TEXT_SECONDARY = new Color(75, 85, 99);
	private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
	private static final Color WARNING_COLOR = new Color(245, 158, 11);
	private static final Color ERROR_COLOR = new Color(239, 68, 68);
	
	// Enhanced font configuration
	private static final Font DEFAULT_FONT = new Font("Segoe UI", Font.PLAIN, 12);
	private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
	private static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 10);

	/**
	 * Applies enhanced theme to the application
	 */
	public static void applyEnhancedTheme()
	{
		long startTime = System.currentTimeMillis();
		log.debug("Starting enhanced UI theme initialization");
		
		try
		{
			// Set Look and Feel with timeout protection
			javax.swing.SwingUtilities.invokeAndWait(() -> {
				try {
					// Set Look and Feel
					UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatIntelliJLaf());
					
					// Configure UI defaults for better appearance
					configureUIDefaults();
					
					log.debug("Look and Feel applied successfully on EDT");
				} catch (Exception e) {
					log.warn("Failed to apply Look and Feel on EDT", e);
					throw new RuntimeException("Theme application failed", e);
				}
			});
			
			long elapsed = System.currentTimeMillis() - startTime;
			log.info("Enhanced UI theme applied successfully in {}ms", elapsed);
		}
		catch (Exception e)
		{
			long elapsed = System.currentTimeMillis() - startTime;
			log.warn("Failed to apply enhanced theme after {}ms, falling back to default", elapsed, e);
			applyFallbackTheme();
		}
	}
	
	/**
	 * Creates an enhanced docking theme with modern styling
	 */
	public static DockingWindowsTheme createEnhancedDockingTheme()
	{
		ShapedGradientDockingTheme theme = new ShapedGradientDockingTheme();
		
		// Customize theme properties
		RootWindowProperties properties = theme.getRootWindowProperties();
		
		// Set modern colors
		properties.getTabWindowProperties()
			.getTabbedPanelProperties()
			.setTabAreaOrientation(net.infonode.util.Direction.UP);
			
		properties.getComponentProperties()
			.setBackgroundColor(BACKGROUND_PRIMARY);
			
		return theme;
	}
	
	/**
	 * Configures UI Manager defaults for enhanced appearance
	 */
	private static void configureUIDefaults()
	{
		// Button styling
		UIManager.put("Button.background", BACKGROUND_SECONDARY);
		UIManager.put("Button.foreground", TEXT_PRIMARY);
		UIManager.put("Button.border", createModernBorder());
		UIManager.put("Button.font", DEFAULT_FONT);
		UIManager.put("Button.focusPainted", false);
		
		// Panel styling
		UIManager.put("Panel.background", BACKGROUND_PRIMARY);
		UIManager.put("Panel.foreground", TEXT_PRIMARY);
		
		// Menu styling
		UIManager.put("Menu.background", BACKGROUND_PRIMARY);
		UIManager.put("Menu.foreground", TEXT_PRIMARY);
		UIManager.put("Menu.font", DEFAULT_FONT);
		UIManager.put("MenuItem.background", BACKGROUND_PRIMARY);
		UIManager.put("MenuItem.foreground", TEXT_PRIMARY);
		UIManager.put("MenuItem.font", DEFAULT_FONT);
		UIManager.put("MenuBar.background", BACKGROUND_SECONDARY);
		UIManager.put("MenuBar.foreground", TEXT_PRIMARY);
		
		// Tab styling
		UIManager.put("TabbedPane.background", BACKGROUND_PRIMARY);
		UIManager.put("TabbedPane.foreground", TEXT_PRIMARY);
		UIManager.put("TabbedPane.font", DEFAULT_FONT);
		UIManager.put("TabbedPane.selectedForeground", ACCENT_COLOR);
		
		// Text component styling
		UIManager.put("TextField.background", Color.WHITE);
		UIManager.put("TextField.foreground", TEXT_PRIMARY);
		UIManager.put("TextField.border", createModernBorder());
		UIManager.put("TextField.font", DEFAULT_FONT);
		
		// Table styling
		UIManager.put("Table.background", Color.WHITE);
		UIManager.put("Table.foreground", TEXT_PRIMARY);
		UIManager.put("Table.gridColor", BORDER_COLOR);
		UIManager.put("Table.font", DEFAULT_FONT);
		UIManager.put("TableHeader.background", BACKGROUND_SECONDARY);
		UIManager.put("TableHeader.foreground", TEXT_PRIMARY);
		UIManager.put("TableHeader.font", HEADER_FONT);
		
		// Scrollbar styling
		UIManager.put("ScrollBar.background", BACKGROUND_SECONDARY);
		UIManager.put("ScrollBar.thumb", ACCENT_COLOR);
		UIManager.put("ScrollBar.track", BACKGROUND_PRIMARY);
		
		// ToolTip styling
		UIManager.put("ToolTip.background", new Color(255, 255, 255, 240));
		UIManager.put("ToolTip.foreground", TEXT_PRIMARY);
		UIManager.put("ToolTip.border", new LineBorder(BORDER_COLOR, 1));
		UIManager.put("ToolTip.font", SMALL_FONT);
	}
	
	/**
	 * Creates a modern border for UI components
	 */
	private static Border createModernBorder()
	{
		return new CompoundBorder(
			new LineBorder(BORDER_COLOR, 1, true),
			new EmptyBorder(4, 8, 4, 8)
		);
	}
	
	/**
	 * Applies fallback theme if enhanced theme fails
	 */
	private static void applyFallbackTheme()
	{
		try
		{
			String systemLaf = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(systemLaf);
			log.info("Fallback theme applied: {}", systemLaf);
		}
		catch (Exception fallbackException)
		{
			log.error("Failed to apply fallback theme", fallbackException);
		}
	}
	
	/**
	 * Gets color for different UI states
	 */
	public static Color getStateColor(UIState state)
	{
		return switch (state)
		{
			case SUCCESS -> SUCCESS_COLOR;
			case WARNING -> WARNING_COLOR;
			case ERROR -> ERROR_COLOR;
			case ACCENT -> ACCENT_COLOR;
			case ACCENT_HOVER -> ACCENT_HOVER;
			case PRIMARY -> TEXT_PRIMARY;
			case SECONDARY -> TEXT_SECONDARY;
		};
	}
	
	/**
	 * UI state enumeration for consistent color usage
	 */
	public enum UIState
	{
		SUCCESS, WARNING, ERROR, ACCENT, ACCENT_HOVER, PRIMARY, SECONDARY
	}
}