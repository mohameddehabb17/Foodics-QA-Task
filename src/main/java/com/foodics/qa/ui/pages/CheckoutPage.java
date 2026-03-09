package com.foodics.qa.ui.pages;

import com.foodics.qa.ui.models.Address;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Page object for checkout address and payment selections.
 */
public class CheckoutPage extends BasePage {
    private static final Logger LOGGER = Logger.getLogger(CheckoutPage.class.getName());
    private static final int AUTOCOMPLETE_RETRY_ATTEMPTS = 3;

    private final By addNewAddressButton = By.id("add-new-address-desktop-sasp-tango-link");
    private final By fullNameField = By.id("address-ui-widgets-enterAddressFullName");
    private final By phoneField = By.id("address-ui-widgets-enterAddressPhoneNumber");
    private final By streetField = By.id("address-ui-widgets-enterAddressLine1");
    private final By buildingField = By.id("address-ui-widgets-enter-building-name-or-number");
    private final By cityField = By.id("address-ui-widgets-enterAddressCity");
    private final By autoCompleteCityOptions = By.id("address-ui-widgets-autocompleteResultsContainer");
    private final By districtField = By.id("address-ui-widgets-enterAddressDistrictOrCounty");
    private final By districtOptions = By.id("address-ui-widgets-autocompleteResultsContainer");
    private final By landmarkField = By.id("address-ui-widgets-landmark");
    private final By homeAddressTypeRadio = By.id("address-ui-widgets-addr-details-res-radio-input");
    private final By addressSubmitButton = By.cssSelector("div#pagelet-layout-section input[data-testid='bottom-continue-button']");
    private final By valuPaymentOption = By.cssSelector("input[name='ppw-instrumentRowSelection'][value*='paymentMethod=Loan']");
    private final By continueAfterPaymentButton = By.cssSelector("input[data-testid='bottom-continue-button']");
    private final By freeShipping = By.xpath("//div[normalize-space(text())='Free Delivery']/following-sibling::div[contains(@class,'a-column') and contains(text(),'EGP')]");
    private final By orderTotal = By.xpath("//span[.//span[contains(text(),'Order total')]]//div[contains(@class,'order-summary-line-definition')]");

    /**
     * Creates checkout page object.
     *
     * @param driver active web driver
     */
    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Handles address creation (if needed) and selects Valu payment option.
     *
     * @param address address details used when add-address flow is visible
     * @return current checkout page
     */
    public CheckoutPage handleAddressAndPayment(Address address) {
        if (isVisible(addNewAddressButton)) {
            addAddress(address);
        }
        selectValuPayment();
        return this;
    }

    /**
     * Reads order total shown on checkout page.
     *
     * @return order total value, or {@code -1} when unavailable
     */
    public int getOrderTotalPrice() {
        return readPrice(orderTotal);
    }

    /**
     * Reads free-shipping discount value shown on checkout page.
     *
     * @return free shipping value, or {@code -1} when unavailable
     */
    public int getFreeShippingPrice() {
        return readPrice(freeShipping);
    }

    private void addAddress(Address address) {
        LOGGER.log(Level.INFO, "[CHECKOUT] Filling add-new-address form.");
        click(addNewAddressButton);
        fillAndSubmitAddressForm(address);
        LOGGER.log(Level.INFO, "[CHECKOUT] Address form submitted.");
    }

    private void fillAndSubmitAddressForm(Address address) {
        type(fullNameField, address.getFullName());
        type(phoneField, address.getPhone());
        type(streetField, address.getStreet());
        type(buildingField, address.getBuilding());

        fillAutoCompleteField(cityField, address.getCity(), autoCompleteCityOptions, "city");

        fillAutoCompleteField(districtField, address.getDistrict(), districtOptions, "district");

        type(landmarkField, address.getLandmark());
        click(homeAddressTypeRadio);
        click(addressSubmitButton);
    }

    private void fillAutoCompleteField(By fieldLocator, String value, By optionsLocator, String fieldName) {
        for (int attempt = 0; attempt < AUTOCOMPLETE_RETRY_ATTEMPTS; attempt++) {
            click(fieldLocator);
            wait.shortPause(500);
            type(fieldLocator, value);

            if (!elements(optionsLocator).isEmpty()) {
                selectFirstAutoCompleteOption(optionsLocator, fieldName);
                return;
            }

            LOGGER.log(Level.FINE, "[CHECKOUT] Options not shown for {0}, retrying click ({1}/{2}).",
                    new Object[]{fieldName, attempt + 1, AUTOCOMPLETE_RETRY_ATTEMPTS});
        }

        LOGGER.log(Level.FINE, "[CHECKOUT] Autocomplete options did not load after retries for {0}.", fieldName);
    }

    private void selectValuPayment() {
        LOGGER.log(Level.INFO, "[CHECKOUT] Selecting Valu payment option.");
        click(valuPaymentOption);
        click(continueAfterPaymentButton);
    }

    private void selectFirstAutoCompleteOption(By optionsLocator, String fieldName) {
        List<WebElement> options = elements(optionsLocator);
        if (!options.isEmpty()) {
            click(options.get(0));
            return;
        }
        LOGGER.log(Level.FINE, "[CHECKOUT] No autocomplete options found for {0}.", fieldName);
    }
}
