package com.foodics.qa.ui.utils;

import com.foodics.qa.shared.config.ConfigManager;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Applies optional pauses between UI test steps for easier debugging.
 */
public class StepPauseUtil {
    private static final Logger LOGGER = Logger.getLogger(StepPauseUtil.class.getName());

    private StepPauseUtil() {
    }

    /**
     * Sleeps for configured delay between test steps.
     *
     * @param stepName current step label for logging
     * @throws IllegalStateException when interrupted
     */
    public static void pauseBetweenSteps(String stepName) {
        long stepDelayMs = resolveStepDelayMs();
        if (stepDelayMs <= 0) {
            return;
        }
        LOGGER.log(Level.INFO, "[STEP-PAUSE] {0} | waiting {1}ms", new Object[]{stepName, stepDelayMs});
        try {
            Thread.sleep(stepDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Step pause interrupted", e);
        }
    }

    /**
     * Resolves delay value from environment or config.
     *
     * @return non-negative delay in milliseconds
     */
    public static long resolveStepDelayMs() {
        String raw = System.getenv("UI_STEP_DELAY_MS");
        if (raw == null || raw.isBlank()) {
            raw = ConfigManager.get("ui.step.delay.ms");
        }
        if (raw == null || raw.isBlank()) {
            return 0L;
        }
        try {
            long value = Long.parseLong(raw.trim());
            return Math.max(0L, value);
        } catch (NumberFormatException ignored) {
            LOGGER.log(Level.FINE, "Invalid step delay value: {0}", raw);
            return 0L;
        }
    }
}
