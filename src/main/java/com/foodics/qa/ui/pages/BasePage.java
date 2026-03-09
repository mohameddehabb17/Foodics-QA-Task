package com.foodics.qa.ui.pages;

import com.foodics.qa.shared.config.ConfigManager;
import com.foodics.qa.ui.utils.GeneralUtils;
import com.foodics.qa.ui.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

/**
 * Base class for page objects with shared Selenium helpers.
 */
public class BasePage {
    protected final WebDriver driver;
    protected final WaitUtils wait;

    /**
     * Creates a page with shared explicit wait configuration.
     *
     * @param driver active web driver
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        int timeout = ConfigManager.getInt("timeout.seconds", 20);
        this.wait = new WaitUtils(driver, timeout);
    }

    protected void click(By by) {
        WebElement element = wait.clickable(by);
        scrollIntoView(element);
        try {
            element.click();
        } catch (ElementClickInterceptedException ex) {
            // Fallback to Actions click for sticky headers/overlays.
            new Actions(driver).moveToElement(element).click().perform();
        }
    }

    protected void click(WebElement element) {
        element = wait.clickable(element);
        scrollIntoView(element);
        try {
            element.click();
        } catch (ElementClickInterceptedException ex) {
            new Actions(driver).moveToElement(element).click().perform();
        }
    }

    protected void type(By by, String value) {
        WebElement element = wait.visible(by);
        element.clear();
        element.sendKeys(value);
    }

    protected String text(By by) {
        return wait.visible(by).getText().trim();
    }

    protected String textContent(WebElement element) {
        if (element == null) {
            return "";
        }
        String content = element. getDomProperty("textContent");
        return content != null ? content.trim() : "";
    }

    protected String text(WebElement element) {
        return element.getText().trim();
    }

    protected List<WebElement> elements(By by) {
        wait.presence(by);
        return driver.findElements(by);
    }

    protected WebElement element(By by) {
        wait.presence(by);
        return driver.findElement(by);
    }

    protected void scrollIntoView(WebElement element) {
        new Actions(driver).moveToElement(element).perform();
    }

    protected void scrollToTop() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
    }

    protected boolean isVisible(By by) {
        try {
            WebElement el = driver.findElement(by);
            return el.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected WebElement findChild(WebElement parent, By childSelector) {
        return parent.findElement(childSelector);
    }

    protected int readPrice(By by) {
        try {
            return GeneralUtils.extractPriceFromText(text(by));
        } catch (RuntimeException exception) {
            return -1;
        }
    }
}

