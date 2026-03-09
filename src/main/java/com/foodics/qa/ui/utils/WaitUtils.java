package com.foodics.qa.ui.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wrapper around Selenium wait operations used by page objects.
 */
public class WaitUtils {
    private static final Logger LOGGER = Logger.getLogger(WaitUtils.class.getName());
    private final WebDriverWait wait;

    /**
     * Creates a wait helper.
     *
     * @param driver active web driver
     * @param timeoutSeconds default timeout in seconds
     */
    public WaitUtils(WebDriver driver, int timeoutSeconds) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    /**
     * Waits until an element is visible.
     *
     * @param by locator
     * @return visible element
     */
    public WebElement visible(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    /**
     * Waits until an element is present in the DOM.
     *
     * @param by locator
     * @return present element
     */
    public WebElement presence(By by) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    /**
     * Waits until an element is clickable.
     *
     * @param by locator
     * @return clickable element
     */
    public WebElement clickable(By by) {
        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    /**
     * Waits until an element is clickable.
     *
     * @param element web element
     * @return clickable element
     */
    public WebElement clickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Waits until URL contains value.
     *
     * @param value expected URL fragment
     * @return {@code true} when condition is met
     */
    public boolean urlContains(String value) {
        return wait.until(ExpectedConditions.urlContains(value));
    }

    /**
     * Waits for exact text and returns the visible text.
     *
     * @param by locator
     * @param expectedText expected exact text
     * @return trimmed element text
     */
    public String waitForText(By by, String expectedText) {
        wait.until(ExpectedConditions.textToBe(by, expectedText));
        return visible(by).getText().trim();
    }

    /**
     * Performs a blocking thread pause.
     *
     * @param milliseconds pause duration in milliseconds
     * @throws IllegalStateException when interrupted
     */
    public void shortPause(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thread sleep was interrupted", exception);
        }
    }

    /**
     * Waits for a custom condition and converts timeout to a false result.
     *
     * @param condition custom condition function
     * @return condition result, or {@code false} on timeout
     */
    public boolean until(Function<WebDriver, Boolean> condition) {
        try {
            return wait.until(condition);
        } catch (TimeoutException exception) {
            LOGGER.log(Level.FINE, "Custom wait timed out: {0}", exception.getMessage());
            return false;
        }
    }
}

