package com.foodics.qa.api.tests.base;

import com.foodics.qa.api.client.UserService;
import com.foodics.qa.shared.config.ConfigManager;
import io.restassured.RestAssured;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Base fixture for API test suites.
 */
public class BaseApiTest {
    protected final UserService userService = new UserService();

    /**
     * Initializes API base URI before class execution.
     */
    @BeforeClass(alwaysRun = true)
    public void setupApi() {
        RestAssured.baseURI = ConfigManager.get("api.base.url");
    }

    /**
     * Clears RestAssured static state after class execution.
     */
    @AfterClass(alwaysRun = true)
    public void tearDownApi() {
        RestAssured.baseURI = null;
        RestAssured.basePath = "";
    }
}

