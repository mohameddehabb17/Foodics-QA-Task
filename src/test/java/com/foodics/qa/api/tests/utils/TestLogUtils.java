package com.foodics.qa.api.tests.utils;

import io.restassured.response.Response;
import org.testng.Reporter;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shared test logging utilities for consistent response logging.
 */
public final class TestLogUtils {
    private static final Logger LOGGER = Logger.getLogger(TestLogUtils.class.getName());

    private TestLogUtils() {
    }

    /**
     * Logs API response details to both Java logger and TestNG report.
     *
     * @param label operation label
     * @param response API response object
     */
    public static void logApiResponse(String label, Response response) {
        String responseBody = response.getBody() == null ? "" : response.getBody().asString();
        String message = String.format("[API][%s] status=%d | body=%s", label, response.statusCode(), responseBody);
        LOGGER.log(Level.INFO, message);
        Reporter.log(message, true);
    }
}
