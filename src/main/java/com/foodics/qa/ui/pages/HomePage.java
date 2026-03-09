package com.foodics.qa.ui.pages;

import com.foodics.qa.ui.utils.GeneralUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Page object for Amazon home page and top-level navigation actions.
 */
public class HomePage extends BasePage {
    private static final Logger LOGGER = Logger.getLogger(HomePage.class.getName());

    private final By cartCount = By.id("nav-cart-count");
    private final By accountMenuLine1 = By.id("nav-link-accountList-nav-line-1");
    private final By allMenuButton = By.id("nav-hamburger-menu");
    private final By menuOverlay = By.cssSelector("div.hmenu-opaque");
    private final By seeAllLink = By.cssSelector("a[aria-label='See All Categories']");
    private final By videoGamesLink = By.xpath("//a[.//div[text()='Video Games']]");
    private final By allVideoGamesLink = By.xpath("//a[normalize-space()='All Video Games']");
    private final String amazonEgHomeUrl = "https://www.amazon.eg/";
    private static final int MAX_STABILIZE_ATTEMPTS = 10;

    /**
     * Creates the home page object.
     *
     * @param driver active web driver
     */
    public HomePage(WebDriver driver) {
        super(driver);
    }

    /**
     * Ensures session is authenticated; performs login when needed.
     *
     * @return current home page instance
     */
    public HomePage ensureLoggedIn() {
        String email = System.getenv("AMAZON_EMAIL");
        String password = System.getenv("AMAZON_PASSWORD");
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new org.testng.SkipException("UI test skipped: set AMAZON_EMAIL and AMAZON_PASSWORD environment variables.");
        }

        openHomePage();
        stabilizeLanding();
        if (isAuthenticated()) {
            return this;
        }

        navigateToLoginPage().login(email, password);
        openHomePage();
        stabilizeLanding();
        if (!isAuthenticated()) {
            throw new IllegalStateException(
                    "Login did not complete successfully. URL=" + driver.getCurrentUrl() + " | title=" + driver.getTitle());
        }

        return this;
    }

    /**
     * Opens the Amazon EG home page.
     *
     * @return current home page instance
     */
    public HomePage openHomePage() {
        LOGGER.log(Level.FINE, "Opening Amazon home page.");
        driver.get(amazonEgHomeUrl);
        stabilizeLanding();
        return this;
    }

    public int getHeaderCartCount() {
        try {
            return GeneralUtils.extractDigitsAsInt(text(cartCount), -1);
        } catch (RuntimeException exception) {
            return -1;
        }
    }

    private boolean isAuthenticated() {
        try {
            String greeting = text(accountMenuLine1).toLowerCase().trim();
            if (greeting.contains("sign in")) {
                return false;
            }
            return !greeting.isBlank();
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private LoginPage navigateToLoginPage() {
        return new LoginPage(driver);
    }

    private void stabilizeLanding() {
        for (int attempt = 0; attempt < MAX_STABILIZE_ATTEMPTS; attempt++) {
            String currentUrl = driver.getCurrentUrl();

            if (currentUrl != null && isUnexpectedLanding(currentUrl)) {
                LOGGER.log(Level.FINE, "Stabilizing landing page (attempt {0}).", attempt + 1);
                driver.get(amazonEgHomeUrl);
                continue;
            }

            return;
        }
    }

    /**
     * Opens the side navigation (hamburger) menu.
     *
     * @return current home page instance
     */
    public HomePage openAllMenu() {
        for (int attempt = 0; attempt < MAX_STABILIZE_ATTEMPTS; attempt++) {
            click(allMenuButton);
            if (isVisible(menuOverlay)) {
                return this;
            }
        }
        return this;
    }

    /**
     * Navigates to Video Games category through side menu.
     *
     * @return current home page instance
     */
    public HomePage clickVideoGames() {
        click(seeAllLink);
        click(videoGamesLink);
        return this;
    }

    /**
     * Opens the all video games listing.
     *
     * @return video games page object
     */
    public VideoGamesPage clickAllVideoGames() {
        click(allVideoGamesLink);
        return new VideoGamesPage(driver);
    }

    private boolean isUnexpectedLanding(String currentUrl) {
        return currentUrl.contains("/ax/claim")
                || currentUrl.contains("amazon.co.uk")
                || driver.getPageSource().contains("Looking for Something?");
    }
}

