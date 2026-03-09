package com.foodics.qa.ui.pages;

import com.foodics.qa.ui.utils.GeneralUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Page object for cart actions and validations.
 */
public class CartPage extends BasePage {
    private static final Logger LOGGER = Logger.getLogger(CartPage.class.getName());

    private final By cartButton = By.cssSelector("#nav-tools > a#nav-cart");
    private final By deleteItemButtons = By.cssSelector("input[data-feature-id='item-delete-button']");
    private final By proceedToCheckoutButton = By.id("sc-buy-box-ptc-button");
    private final By subtotalAmount = By.cssSelector("span#sc-subtotal-amount-buybox span[class*='sc-price']");
    private final By itemNames = By.cssSelector("li[class*='sc-item-product-title-cont'] span[class*='a-truncate-full']");

    private final HomePage homePage;

    /**
     * Creates cart page object.
     *
     * @param driver active web driver
     */
    public CartPage(WebDriver driver) {
        super(driver);
        this.homePage = new HomePage(driver);
    }

    /**
     * Opens cart from header navigation.
     *
     * @return current cart page
     */
    public CartPage open() {
        scrollToTop();
        click(cartButton);
        return this;
    }

    /**
     * Removes all cart items until header count reaches zero.
     *
     * @return current cart page
     * @throws IllegalStateException when item removal fails
     */
    private void ensureEmptyCart() {
        int initialCount = homePage.getHeaderCartCount();
        if (initialCount == 0) {
            LOGGER.log(Level.INFO, "[CART-CLEANUP] Header shows 0 items; skipping cleanup.");
            return;
        }

        LOGGER.log(Level.INFO, "[CART-CLEANUP] Header shows {0} item(s); cleaning cart.", initialCount);
        open();

        int round = 0;
        while (true) {
            int beforeCount = homePage.getHeaderCartCount();
            if (beforeCount == 0) {
                LOGGER.log(Level.INFO, "[CART-CLEANUP] completed. header=0.");
                return;
            }

            if (!removeOneItem()) {
                throw new IllegalStateException("Could not remove next cart item while header count is " + beforeCount);
            }

            boolean decremented = wait.until(driver -> GeneralUtils.isLessThan(homePage.getHeaderCartCount(), beforeCount));
            int afterCount = homePage.getHeaderCartCount();
            if (!decremented) {
                throw new IllegalStateException("Cart count did not decrement after delete. before=" + beforeCount + " after=" + afterCount);
            }

            LOGGER.log(Level.INFO,
                    "[CART-CLEANUP] round={0} | before={1} | after={2} | removed=true",
                    new Object[]{round, beforeCount, afterCount});
            round++;
        }
    }

    /**
     * Ensures cart is empty before shopping flow starts and returns to home page.
     *
     * @return current cart page
     */
    public CartPage ensureEmptyBeforeShopping() {
        if (homePage.getHeaderCartCount() <= 0) {
            return this;
        }

        ensureEmptyCart();
        homePage.openHomePage();
        return this;
    }

    /**
     * Proceeds to checkout from cart page.
     *
     * @return checkout page object
     */
    public CheckoutPage proceedToCheckout() {
        click(proceedToCheckoutButton);
        return new CheckoutPage(driver);
    }

    /**
     * Reads subtotal price shown in cart.
     *
     * @return subtotal value, or {@code -1} when unavailable
     */
    public int getSubtotalPrice() {
        return readPrice(subtotalAmount);
    }

    /**
     * Returns displayed item names in cart.
     *
     * @return cart item names as shown in UI
     */
    public List<String> getCartItemNames() {
        List<WebElement> itemElements = elements(itemNames);
        List<String> names = new ArrayList<>();
        for (WebElement element : itemElements) {
            String name = textContent(element);
            if (name != null && !name.isBlank()) {
                names.add(name.trim());
            }
        }
        return names;
    }

    private boolean removeOneItem() {
        try {
            click(deleteItemButtons);
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }
}
