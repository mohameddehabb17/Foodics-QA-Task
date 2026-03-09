package com.foodics.qa.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object for video games category filters and listing navigation.
 */
public class VideoGamesPage extends BasePage {
    private final By freeShippingFilter = By.xpath("//a[.//span[normalize-space()='Free Shipping']]");
    private final By conditionNewFilter = By.xpath("//a[.//span[normalize-space()='New']]");

    /**
     * Creates video games page object.
     *
     * @param driver active web driver
     */
    public VideoGamesPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Applies free shipping filter.
     *
     * @return current page instance
     */
    public VideoGamesPage applyFreeShippingFilter() {
        click(freeShippingFilter);
        return this;
    }

    /**
     * Applies "New" condition filter.
     *
     * @return current page instance
     */
    public VideoGamesPage applyNewConditionFilter() {
        click(conditionNewFilter);
        return this;
    }

    /**
     * Opens listing page object after filters are applied.
     *
     * @return product listing page
     */
    public ProductListingPage openListing() {
        return new ProductListingPage(driver);
    }
}

