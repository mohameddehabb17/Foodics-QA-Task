package com.foodics.qa.ui.pages;

import com.foodics.qa.ui.utils.GeneralUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Page object for product listing interactions such as sorting, scanning, and pagination.
 */
public class ProductListingPage extends BasePage {
    private final By sortDropdown = By.id("s-result-sort-select");
    private final By priceHighToLowVisible = By.xpath(
            "//div[contains(@class,'a-popover') and contains(@class,'a-dropdown') and @aria-hidden='false']"
                    + "//a[contains(text(),'Price: High to Low')]"
    );
    private final By headerCartCount = By.id("nav-cart-count");
    private final By productCards = By.cssSelector("div[data-component-type='s-search-result']");
    private final By productName = By.cssSelector("h2 span");
    private final By priceWhole = By.cssSelector("span.a-price-whole");
    private final By addToCartButton = By.name("submit.addToCart");
    private final By nextPageCandidates = By.cssSelector("a.s-pagination-next:not(.s-pagination-disabled)");
    private final By subtotalAmount = By.cssSelector("div.ewc-subtotal-value span.ewc-subtotal-amount h2");

    private final HomePage homePage;

    /**
     * Creates listing page object.
     *
     * @param driver active web driver
     */
    public ProductListingPage(WebDriver driver) {
        super(driver);
        this.homePage = new HomePage(driver);
    }

    /**
     * Sorts listing by highest price first.
     *
     * @return current listing page
     */
    public ProductListingPage sortByPriceHighToLow() {
        click(sortDropdown);
        click(priceHighToLowVisible);
        return this;
    }

    /**
     * Adds distinct products priced below or equal to {@code maxPrice} until target count is reached.
     *
     * @param maxPrice maximum accepted product price
     * @param targetCartCount desired cart items count
     * @return names of products added from the listing
     */
    public List<String> addProductsBelow(int maxPrice, int targetCartCount) {
        List<String> addedProducts = new ArrayList<>();

        while (homePage.getHeaderCartCount() < targetCartCount) {
            wait.visible(productCards);
            List<WebElement> cards = elements(productCards);

            for (WebElement card : cards) {
                int price = extractCardPrice(card);
                if (price <= 0 || price > maxPrice) {
                    continue;
                }

                String name = extractCardName(card);
                if (name == null || addedProducts.contains(name)) {
                    continue;
                }

                WebElement addButton = extractAddToCartButton(card);
                if (addButton == null) {
                    continue;
                }

                int cartBefore = homePage.getHeaderCartCount();
                int subtotalBefore = cartBefore > 0 ? getSubtotalPrice() : 0;

                click(addButton);
                wait.waitForText(headerCartCount, String.valueOf(cartBefore + 1));
                wait.until(d -> GeneralUtils.isMoreThan(getSubtotalPrice(), subtotalBefore));

                addedProducts.add(name);

                if (homePage.getHeaderCartCount() >= targetCartCount) {
                    return addedProducts;
                }
            }

            click(nextPageCandidates);
        }

        return addedProducts;
    }

    /**
     * Reads listing subtotal text and parses it as integer.
     *
     * @return subtotal value, or {@code -1} when unavailable
     */
    public int getSubtotalPrice() {
        return readPrice(subtotalAmount);
    }

    private int extractCardPrice(WebElement card) {
        try {
            WebElement priceElement = findChild(card, priceWhole);
            return GeneralUtils.extractPriceFromText(text(priceElement));
        } catch (RuntimeException ignored) {
            return -1;
        }
    }

    private String extractCardName(WebElement card) {
        try {
            WebElement nameElement = findChild(card, productName);
            return nameElement == null ? null : text(nameElement);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private WebElement extractAddToCartButton(WebElement card) {
        try {
            WebElement addBtn = findChild(card, addToCartButton);
            return addBtn.isDisplayed() ? addBtn : null;
        } catch (RuntimeException ignored) {
            return null;
        }
    }
}
