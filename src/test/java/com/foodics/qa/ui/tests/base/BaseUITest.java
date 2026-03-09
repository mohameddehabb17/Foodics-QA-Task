package com.foodics.qa.ui.tests.base;

import com.foodics.qa.shared.config.ConfigManager;
import com.foodics.qa.ui.driver.DriverFactory;
import com.foodics.qa.ui.tests.utils.AllureUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Base fixture for UI tests handling driver lifecycle and failure diagnostics.
 */
public class BaseUITest {

    /**
     * Initializes browser session and opens configured UI base URL.
     */
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverFactory.initDriver();
        getDriver().get(ConfigManager.get("ui.base.url"));
    }

    /**
     * Attaches diagnostics on failures and closes the browser session.
     *
     * @param testResult executed test result
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult testResult) {
        try {
            if (testResult.getStatus() == ITestResult.FAILURE) {
                try {
                    AllureUtils.attachScreenshot(getDriver());
                    AllureUtils.attachText("Failed test: " + testResult.getName());
                    AllureUtils.attachCurrentUrl(getDriver());
                    AllureUtils.attachPageSource(getDriver());
                } catch (WebDriverException ignored) {
                    // Browser session may already be terminated by remote/web app behavior.
                }
            }
        } finally {
            DriverFactory.quitDriver();
        }
    }

    /**
     * Returns the active test driver.
     *
     * @return active web driver
     */
    protected WebDriver getDriver() {
        return DriverFactory.getDriver();
    }
}
