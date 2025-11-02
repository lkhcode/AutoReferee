/*
 * Copyright (c) 2009 - 2025, DHBW Mannheim - TIGERs Mannheim
 */
package edu.tigers.sumatra.diagnostics;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Startup diagnostics utility to track application initialization progress
 * and detect potential startup issues.
 */
@Log4j2
public class StartupDiagnostics
{
	private static final long STARTUP_TIMEOUT_MS = 30000; // 30 seconds
	private static volatile boolean diagnosticsStarted = false;
	private static volatile boolean startupCompleted = false;
	private static long startupStartTime = 0;
	private static CompletableFuture<Void> startupFuture = null;
	
	/**
	 * Marks the beginning of application startup
	 */
	public static void beginStartupDiagnostics()
	{
		if (diagnosticsStarted) {
			log.warn("Startup diagnostics already started, ignoring duplicate call");
			return;
		}
		
		startupStartTime = System.currentTimeMillis();
		diagnosticsStarted = true;
		startupCompleted = false;
		
		log.info("=== Application Startup Diagnostics Begin ===");
		
		// Start watchdog timer for startup timeout
		startupFuture = CompletableFuture.runAsync(() -> {
			try {
				Thread.sleep(STARTUP_TIMEOUT_MS);
				if (!startupCompleted) {
					long elapsed = System.currentTimeMillis() - startupStartTime;
					log.error("=== Application Startup TIMEOUT after {}ms ===", elapsed);
					log.error("Application may be stuck during initialization. Check for:");
					log.error("- EDT (Event Dispatch Thread) blocks");
					log.error("- Deadlocks in UI initialization");
					log.error("- Resource loading issues");
					log.error("- Network timeouts during initialization");
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});
	}
	
	/**
	 * Marks the completion of application startup
	 */
	public static void completeStartupDiagnostics()
	{
		if (!diagnosticsStarted) {
			log.warn("Startup diagnostics not started, cannot complete");
			return;
		}
		
		if (startupCompleted) {
			log.warn("Startup diagnostics already completed, ignoring duplicate call");
			return;
		}
		
		startupCompleted = true;
		long elapsed = System.currentTimeMillis() - startupStartTime;
		
		// Cancel the timeout watchdog
		if (startupFuture != null) {
			startupFuture.cancel(true);
		}
		
		log.info("=== Application Startup Completed in {}ms ===", elapsed);
		
		// Log performance analysis
		if (elapsed > 5000) {
			log.warn("Slow startup detected ({}ms). Consider optimizing initialization", elapsed);
		} else if (elapsed > 2000) {
			log.info("Startup time acceptable ({}ms)", elapsed);
		} else {
			log.info("Fast startup ({}ms)", elapsed);
		}
	}
	
	/**
	 * Logs a checkpoint during startup for tracking progress
	 */
	public static void checkpoint(String description)
	{
		if (!diagnosticsStarted) {
			return;
		}
		
		long elapsed = System.currentTimeMillis() - startupStartTime;
		log.debug("Startup checkpoint [{}ms]: {}", elapsed, description);
	}
	
	/**
	 * Checks if startup is taking too long and logs a warning
	 */
	public static void checkStartupProgress()
	{
		if (!diagnosticsStarted || startupCompleted) {
			return;
		}
		
		long elapsed = System.currentTimeMillis() - startupStartTime;
		if (elapsed > 10000) { // 10 seconds
			log.warn("Startup is taking longer than expected: {}ms", elapsed);
		}
	}
	
	/**
	 * Reset diagnostics state (for testing purposes)
	 */
	public static void reset()
	{
		diagnosticsStarted = false;
		startupCompleted = false;
		startupStartTime = 0;
		if (startupFuture != null) {
			startupFuture.cancel(true);
			startupFuture = null;
		}
	}
	
	/**
	 * Gets startup elapsed time
	 */
	public static long getElapsedTime()
	{
		if (!diagnosticsStarted) {
			return 0;
		}
		return System.currentTimeMillis() - startupStartTime;
	}
	
	/**
	 * Checks if startup has completed
	 */
	public static boolean isStartupCompleted()
	{
		return startupCompleted;
	}
}