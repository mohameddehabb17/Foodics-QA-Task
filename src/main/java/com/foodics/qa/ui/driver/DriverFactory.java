package com.foodics.qa.ui.driver;

import com.foodics.qa.shared.config.ConfigManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates and manages one WebDriver instance per test thread.
 */
public final class DriverFactory {
    private static final Logger LOGGER = Logger.getLogger(DriverFactory.class.getName());
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    /**
     * Initializes the thread-local browser driver using configured browser and timeouts.
     */
    public static void initDriver() {
        String browser = ConfigManager.get("browser");
        boolean headless = ConfigManager.getBoolean("headless", false);
        int pageLoadTimeoutSeconds = ConfigManager.getInt("page.load.timeout.seconds", 120);

        WebDriver webDriver;
        if ("firefox".equalsIgnoreCase(browser)) {
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions options = new FirefoxOptions();
            String profileDir = buildProfileDir("firefox-profile");
            options.addPreference("intl.accept_languages", "en-US");
            options.addPreference("dom.webnotifications.enabled", false);
            options.addPreference("permissions.default.desktop-notification", 2);
            options.addPreference("browser.shell.checkDefaultBrowser", false);
            options.addArguments("-profile", profileDir);
            LOGGER.log(Level.INFO, "Using persistent Firefox profile at: {0}", profileDir);
            if (headless) {
                options.addArguments("-headless");
            }
            webDriver = new FirefoxDriver(options);
        } else {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            String profileDir = buildProfileDir("chrome-profile");
            options.addArguments("--lang=en");
            options.addArguments("--disable-notifications");
            options.addArguments("--no-default-browser-check");
            options.addArguments("--user-data-dir=" + profileDir);
            options.addArguments("--profile-directory=Default");
            LOGGER.log(Level.INFO, "Using persistent Chrome profile at: {0}", profileDir);
            if (headless) {
                options.addArguments("--headless=new");
            }
            webDriver = new ChromeDriver(options);
        }

        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeoutSeconds));
        DRIVER.set(webDriver);
    }

    private static String buildProfileDir(String profileFolderName) {
        String profileDir = System.getProperty("user.dir") + File.separator
                + ".selenium" + File.separator + profileFolderName;
        File directory = new File(profileDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return profileDir;
    }

    /**
     * Returns active thread-local driver.
     *
     * @return active driver instance
     * @throws IllegalStateException when driver is not initialized
     */
    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver is not initialized for the current thread");
        }
        return driver;
    }

    /**
     * Quits and clears current thread-local driver.
     */
    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}

