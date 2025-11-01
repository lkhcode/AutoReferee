/*
 * Copyright (c) 2009 - 2025, DHBW Mannheim - TIGERs Mannheim
 */

package edu.tigers.sumatra.ui;

import lombok.extern.log4j.Log4j2;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

/**
 * Enhanced status display component for showing notifications and status updates.
 * Provides smooth animations and modern styling for better user experience.
 */
@Log4j2
public class EnhancedStatusDisplay extends JPanel
{
	private static final int CORNER_RADIUS = 8;
	private static final int FADE_DURATION = 300; // milliseconds
	private static final int DISPLAY_DURATION = 3000; // milliseconds
	
	private final JLabel statusLabel;
	private final Timer fadeTimer;
	private float opacity = 1.0f;
	private boolean fadeOut = false;
	private EnhancedUITheme.UIState currentState = EnhancedUITheme.UIState.PRIMARY;
	
	public EnhancedStatusDisplay()
	{
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(8, 12, 8, 12));
		setOpaque(false);
		
		statusLabel = new JLabel("Ready");
		statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		statusLabel.setForeground(Color.WHITE);
		add(statusLabel, BorderLayout.CENTER);
		
		// Timer for fade animations
		fadeTimer = new Timer(16, new FadeAnimator()); // ~60fps
		
		// Initial state
		showStatus("Application Ready", EnhancedUITheme.UIState.SUCCESS);
	}
	
	/**
	 * Shows a status message with specified state
	 */
	public void showStatus(String message, EnhancedUITheme.UIState state)
	{
		showStatus(message, state, DISPLAY_DURATION);
	}
	
	/**
	 * Shows a status message with specified state and duration
	 */
	public void showStatus(String message, EnhancedUITheme.UIState state, int duration)
	{
		currentState = state;
		statusLabel.setText(message);
		
		// Reset opacity and show
		opacity = 1.0f;
		fadeOut = false;
		setVisible(true);
		repaint();
		
		// Schedule fade out
		Timer displayTimer = new Timer(duration, e -> startFadeOut());
		displayTimer.setRepeats(false);
		displayTimer.start();
		
		log.trace("Status displayed: {} ({})", message, state);
	}
	
	/**
	 * Shows success message
	 */
	public void showSuccess(String message)
	{
		showStatus(message, EnhancedUITheme.UIState.SUCCESS);
	}
	
	/**
	 * Shows warning message
	 */
	public void showWarning(String message)
	{
		showStatus(message, EnhancedUITheme.UIState.WARNING);
	}
	
	/**
	 * Shows error message with longer display time
	 */
	public void showError(String message)
	{
		showStatus(message, EnhancedUITheme.UIState.ERROR, DISPLAY_DURATION * 2);
	}
	
	/**
	 * Starts fade out animation
	 */
	private void startFadeOut()
	{
		fadeOut = true;
		fadeTimer.start();
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g.create();
		
		try
		{
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			// Get background color based on current state
			Color baseColor = EnhancedUITheme.getStateColor(currentState);
			Color backgroundColor = new Color(
				baseColor.getRed(),
				baseColor.getGreen(), 
				baseColor.getBlue(),
				(int) (opacity * 200) // Apply opacity
			);
			
			// Draw rounded rectangle background
			RoundRectangle2D.Float background = new RoundRectangle2D.Float(
				0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS
			);
			
			g2d.setColor(backgroundColor);
			g2d.fill(background);
			
			// Draw subtle border
			Color borderColor = new Color(
				baseColor.getRed(),
				baseColor.getGreen(),
				baseColor.getBlue(),
				(int) (opacity * 120)
			);
			g2d.setColor(borderColor);
			g2d.draw(background);
		}
		finally
		{
			g2d.dispose();
		}
		
		super.paintComponent(g);
	}
	
	/**
	 * Animation handler for fade effects
	 */
	private class FadeAnimator implements ActionListener
	{
		private static final float FADE_SPEED = 0.05f;
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (fadeOut)
			{
				opacity -= FADE_SPEED;
				if (opacity <= 0)
				{
					opacity = 0;
					fadeTimer.stop();
					setVisible(false);
				}
			}
			else
			{
				opacity += FADE_SPEED;
				if (opacity >= 1.0f)
				{
					opacity = 1.0f;
					fadeTimer.stop();
				}
			}
			
			// Update label opacity
			Color labelColor = statusLabel.getForeground();
			statusLabel.setForeground(new Color(
				labelColor.getRed(),
				labelColor.getGreen(),
				labelColor.getBlue(),
				(int) (opacity * 255)
			));
			
			repaint();
		}
	}
}