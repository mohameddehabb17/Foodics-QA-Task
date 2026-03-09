package com.foodics.qa.ui.tests.utils;

import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Allure attachment helpers for UI test diagnostics.
 */
public final class AllureUtils {
    private AllureUtils() {
    }

    /**
     * Captures and attaches screenshot bytes.
     *
     * @param driver active web driver
     * @return screenshot bytes, or empty array when driver is null
     */
    @Attachment(value = "Failure Screenshot", type = "image/png")
    public static byte[] attachScreenshot(WebDriver driver) {
        if (driver == null) {
            return new byte[0];
        }
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    /**
     * Attaches plain text to Allure report.
     *
     * @param value attachment value
     * @return same text value
     */
    @Attachment(value = "Text Attachment", type = "text/plain")
    public static String attachText(String value) {
        return value;
    }

    /**
     * Attaches current browser URL.
     *
     * @param driver active web driver
     * @return current URL, or empty value when driver is null
     */
    @Attachment(value = "Current URL", type = "text/plain")
    public static String attachCurrentUrl(WebDriver driver) {
        if (driver == null) {
            return "";
        }
        return driver.getCurrentUrl();
    }

    /**
     * Attaches current page source.
     *
     * @param driver active web driver
     * @return HTML source, or empty value when driver is null
     */
    @Attachment(value = "Page Source", type = "text/html")
    public static String attachPageSource(WebDriver driver) {
        if (driver == null) {
            return "";
        }
        return driver.getPageSource();
    }
}
