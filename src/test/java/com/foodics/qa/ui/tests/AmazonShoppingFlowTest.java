package com.foodics.qa.ui.tests;

import com.foodics.qa.ui.models.Address;
import com.foodics.qa.ui.pages.CartPage;
import com.foodics.qa.ui.pages.CheckoutPage;
import com.foodics.qa.ui.pages.HomePage;
import com.foodics.qa.ui.pages.ProductListingPage;
import com.foodics.qa.ui.pages.VideoGamesPage;
import com.foodics.qa.ui.tests.base.BaseUITest;
import com.foodics.qa.ui.utils.StepPauseUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * End-to-end UI flow for filtering video games, adding products, and validating checkout totals.
 */
public class AmazonShoppingFlowTest extends BaseUITest {
    private static final Logger LOGGER = Logger.getLogger(AmazonShoppingFlowTest.class.getName());

    private static final int MAX_ITEMS_TO_ADD = 5;
    private static final int MAX_ALLOWED_PRICE = 15000;

    /**
     * Validates the shopping flow through checkout up to payment selection without placing the order.
     */
    @Test
    public void testCompleteAmazonVideoGamesFlowWithoutPlacingOrder() {
        List<String> addedProductNames = new ArrayList<>();
        List<String> cartProductNames = new ArrayList<>();

        HomePage homePage = new HomePage(getDriver());
        CartPage cartPage = new CartPage(getDriver());

        // Step 1: Start as a logged-in shopper and make sure the cart is empty.
        homePage.ensureLoggedIn();
        StepPauseUtil.pauseBetweenSteps("after ensureLoggedIn");

        LOGGER.log(Level.INFO, "[CART-CHECK] headerCount={0}", homePage.getHeaderCartCount());
        cartPage.ensureEmptyBeforeShopping();
        StepPauseUtil.pauseBetweenSteps("after ensureEmptyBeforeShopping");

        // Step 2: Go to Video Games, apply business filters, and sort by highest price.
        VideoGamesPage videoGamesPage = homePage.openAllMenu().clickVideoGames().clickAllVideoGames();
        StepPauseUtil.pauseBetweenSteps("after open Video Games listing");

        videoGamesPage.applyFreeShippingFilter().applyNewConditionFilter();
        StepPauseUtil.pauseBetweenSteps("after applying filters");

        ProductListingPage productListingPage = videoGamesPage.openListing();
        productListingPage.sortByPriceHighToLow();
        StepPauseUtil.pauseBetweenSteps("after sort high to low");

        // Step 3: Add up to MAX_ITEMS_TO_ADD products below MAX_ALLOWED_PRICE EGP, then validate that products were found.
        addedProductNames.addAll(productListingPage.addProductsBelow(MAX_ALLOWED_PRICE, MAX_ITEMS_TO_ADD));
        Collections.sort(addedProductNames);
        LOGGER.log(Level.INFO, "[CART-NAMES] Added: {0}", addedProductNames);

        Assert.assertFalse(addedProductNames.isEmpty(), "No products below " + MAX_ALLOWED_PRICE + " EGP were found in scanned pages.");
        LOGGER.log(Level.INFO, "[SCAN-FINAL] totalCollected={0}", addedProductNames.size());

        int subtotalFromListingPage = productListingPage.getSubtotalPrice();
        LOGGER.log(Level.INFO, "[PRICE] Subtotal after adding in listing page: {0}", subtotalFromListingPage);

        // Step 4: Confirm the cart count before opening the cart page.
        int headerCartCountBeforeOpeningCart = homePage.getHeaderCartCount();
        LOGGER.log(Level.INFO, "[CART-GATE] headerCount={0}, expected={1}, when={2}",
            new Object[]{headerCartCountBeforeOpeningCart, MAX_ITEMS_TO_ADD, "before opening cart"});
        Assert.assertEquals(
            headerCartCountBeforeOpeningCart,
            MAX_ITEMS_TO_ADD,
            "Cart header shows " + headerCartCountBeforeOpeningCart + " item(s) before opening cart - expected exactly " + MAX_ITEMS_TO_ADD + "."
        );

        // Step 5: Open cart, compare product names, and validate subtotal continuity.
        cartPage.open();
        StepPauseUtil.pauseBetweenSteps("after open cart");

        cartProductNames.addAll(cartPage.getCartItemNames());
        Collections.sort(cartProductNames);
        LOGGER.log(Level.INFO, "[CART-NAMES] Cart: {0}", cartProductNames);

        int headerCartCountBeforeCheckout = homePage.getHeaderCartCount();
        LOGGER.log(Level.INFO, "[CART-GATE] headerCount={0}, expected={1}, when={2}",
            new Object[]{headerCartCountBeforeCheckout, MAX_ITEMS_TO_ADD, "before checkout"});
        Assert.assertEquals(
            headerCartCountBeforeCheckout,
            MAX_ITEMS_TO_ADD,
            "Cart header shows " + headerCartCountBeforeCheckout + " item(s) before checkout - expected exactly " + MAX_ITEMS_TO_ADD + "."
        );

        int subtotalFromCartPage = cartPage.getSubtotalPrice();
        LOGGER.log(Level.INFO, "[PRICE] Subtotal in cart page: {0}", subtotalFromCartPage);

        Assert.assertEquals(subtotalFromListingPage, subtotalFromCartPage, "Subtotal in listing and cart page should match");
        Assert.assertEquals(cartProductNames, addedProductNames, "Cart item names should match added products");

        // Step 6: Move to checkout, fill address/payment details, and verify totals and shipping values.
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        StepPauseUtil.pauseBetweenSteps("after proceed to checkout");

        int subtotalFromCheckoutPageBeforePayment = checkoutPage.getOrderTotalPrice();
        LOGGER.log(Level.INFO, "[PRICE] Subtotal/order total in checkout page (before payment): {0}", subtotalFromCheckoutPageBeforePayment);

        Address checkoutAddress = new Address(
            "Mohamed Ehab",
            "1003667700",
            "El-Wehda Street",
            "Building 1",
            "Giza",
            "Imbaba-Kerdasah",
            "Giza",
            "Dandy mall"
        );

        checkoutPage.handleAddressAndPayment(checkoutAddress);
        StepPauseUtil.pauseBetweenSteps("after address and payment selection");

        int orderTotalAfterPaymentSelection = checkoutPage.getOrderTotalPrice();
        LOGGER.log(Level.INFO, "[PRICE] Order total in checkout page (after payment): {0}", orderTotalAfterPaymentSelection);

        Assert.assertEquals(subtotalFromCheckoutPageBeforePayment, orderTotalAfterPaymentSelection, "Subtotal in checkout page and order total after payment should match");

        int freeShipping = checkoutPage.getFreeShippingPrice();
        LOGGER.log(Level.INFO, "[PRICE] Free shipping: {0}", freeShipping);
        Assert.assertTrue(freeShipping < 0, "Free shipping should be  < 0");
    }
}
