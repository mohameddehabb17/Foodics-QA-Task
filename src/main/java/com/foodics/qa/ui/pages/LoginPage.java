package com.foodics.qa.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Page object for Amazon sign-in flow.
 */
public class LoginPage extends BasePage {
    private static final Logger LOGGER = Logger.getLogger(LoginPage.class.getName());

    private final By accountList = By.id("nav-link-accountList");
    private final By emailField = By.id("ap_email_login");
    private final By continueButton = By.id("continue");
    private final By passwordField = By.id("ap_password");
    private final By signInButton = By.id("signInSubmit");
    private final String amazonEgDirectSignInUrl = "https://www.amazon.eg/-/en/ap/signin?openid.pape.max_auth_age=0&openid.return_to=https%3A%2F%2Fwww.amazon.eg%2F%3Fref_%3Dnav_signin&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.assoc_handle=egflex&openid.mode=checkid_setup&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0";
    private static final int MAX_STABILIZE_ATTEMPTS = 10;

    /**
     * Creates the login page object.
     *
     * @param driver active web driver
     */
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Logs in using supplied credentials.
     *
     * @param email account email
     * @param password account password
     * @return authenticated home page
     * @throws IllegalStateException when login cannot be completed after retries
     */
    public HomePage login(String email, String password) {
        LOGGER.log(Level.INFO, "Starting login flow.");
        click(accountList);

        for (int attempt = 0; attempt < MAX_STABILIZE_ATTEMPTS; attempt++) {
            if (stabilizeSignin()) {
                continue;
            }

            type(emailField, email);
            click(continueButton);

            type(passwordField, password);
            click(signInButton);

            if (stabilizeSignin()) {
                continue;
            }

            LOGGER.log(Level.INFO, "Login flow completed successfully.");
            return new HomePage(driver);
        }

        throw new IllegalStateException("Unable to complete login after repeated sign-in redirects.");
    }

    private boolean stabilizeSignin() {
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl != null && isUnexpectedSignInRedirect(currentUrl)) {
            LOGGER.log(Level.FINE, "Detected intermediate sign-in page; redirecting to direct Amazon EG sign-in.");
            driver.get(amazonEgDirectSignInUrl);
            return true;
        }
        return false;
    }

    private boolean isUnexpectedSignInRedirect(String currentUrl) {
        return currentUrl.contains("/ax/claim")
                || currentUrl.contains("amazon.co.uk")
                || driver.getPageSource().contains("Looking for Something?");
    }
}

