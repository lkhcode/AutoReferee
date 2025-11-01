/*
 * Copyright (c) 2009 - 2022, DHBW Mannheim - TIGERs Mannheim
 */
package edu.tigers.sumatra.views;

import lombok.Getter;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GradientPaint;
import java.awt.geom.RoundRectangle2D;


/**
 * Modern, enhanced icon for Sumatra views with gradient effects and rounded corners.
 * Provides a more polished and professional appearance for the UI tabs.
 */
@Getter
public class ViewIcon implements Icon
{
	private static final int ICON_SIZE = 12; // Slightly larger for better visibility
	private static final int CORNER_RADIUS = 3;
	private static final Color BORDER_COLOR = new Color(80, 80, 80, 180);
	private static final Color GRADIENT_START = new Color(120, 220, 120, 200);
	private static final Color GRADIENT_END = new Color(80, 180, 80, 220);
	private static final Color HIGHLIGHT = new Color(255, 255, 255, 40);
	
	private final int iconHeight = ICON_SIZE;
	private final int iconWidth = ICON_SIZE;


	@Override
	public void paintIcon(final Component c, final Graphics g, final int x, final int y)
	{
		Graphics2D g2d = (Graphics2D) g.create();
		
		try 
		{
			// Enable antialiasing for smooth edges
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			
			// Create rounded rectangle shape
			RoundRectangle2D.Float iconShape = new RoundRectangle2D.Float(
					x, y, ICON_SIZE, ICON_SIZE, CORNER_RADIUS, CORNER_RADIUS);
			
			// Create gradient paint for modern look
			GradientPaint gradient = new GradientPaint(
					x, y, GRADIENT_START,
					x, y + ICON_SIZE, GRADIENT_END);
			
			// Fill with gradient
			g2d.setPaint(gradient);
			g2d.fill(iconShape);
			
			// Add subtle highlight on top
			RoundRectangle2D.Float highlight = new RoundRectangle2D.Float(
					x + 1, y + 1, ICON_SIZE - 2, ICON_SIZE / 2 - 1, CORNER_RADIUS, CORNER_RADIUS);
			g2d.setColor(HIGHLIGHT);
			g2d.fill(highlight);
			
			// Draw border
			g2d.setColor(BORDER_COLOR);
			g2d.draw(iconShape);
		}
		finally 
		{
			g2d.dispose();
		}
	}
}
