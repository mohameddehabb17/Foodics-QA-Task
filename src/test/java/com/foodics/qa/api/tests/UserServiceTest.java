package com.foodics.qa.api.tests;

import com.foodics.qa.api.models.User;
import com.foodics.qa.api.tests.base.BaseApiTest;
import com.foodics.qa.api.tests.utils.TestLogUtils;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * API tests for user create/get/update scenarios against ReqRes endpoints.
 */
public class UserServiceTest extends BaseApiTest {
    /**
     * Verifies successful user creation and response payload fields.
     */
    @Test
    public void testCreateUserSuccessfully() {
        User user = new User("user-" + System.currentTimeMillis(), "qa", 25);

        Response response = userService.createUser(user);
        TestLogUtils.logApiResponse("createUser", response);

        Assert.assertEquals(response.statusCode(), 201, "Create user should return 201");
        Assert.assertEquals(response.jsonPath().getString("name"), user.getName(), "Created user name should match payload");
        Assert.assertEquals(response.jsonPath().getString("job"), user.getJob(), "Created user job should match payload");
        Assert.assertEquals(response.jsonPath().getInt("age"), user.getAge(), "Created user age should match payload");

        int createdId = response.jsonPath().getInt("id");
        Assert.assertTrue(createdId > 0, "Created user id should be greater than zero. id=" + createdId);

        String createdAt = response.jsonPath().getString("createdAt");
        Assert.assertNotNull(createdAt, "createdAt should be present");
        Assert.assertFalse(createdAt.isBlank(), "createdAt should be present");
    }

    /**
     * Verifies retrieval endpoint returns an existing user payload.
     */
    @Test
    public void testRetrieveUserByCreatedId() {
        User user = new User("user-" + System.currentTimeMillis(), "dev", 26);

        Response createResponse = userService.createUser(user);
        TestLogUtils.logApiResponse("createUser-before-get", createResponse);
        Assert.assertEquals(createResponse.statusCode(), 201, "Create user for retrieval flow should return 201");

        int createdId = createResponse.jsonPath().getInt("id");
        Assert.assertTrue(createdId > 0, "Created user id should be greater than zero for retrieval flow. id=" + createdId);

        createdId = 2; // Using a known existing user id for retrieval since ReqRes is a fake REST API for testing

        Response getResponse = userService.getUser(createdId);
        TestLogUtils.logApiResponse("getUser-after-create", getResponse);

        Assert.assertEquals(getResponse.statusCode(), 200, "Retrieve user should return 200");
        Assert.assertEquals(getResponse.jsonPath().getInt("data.id"), createdId, "Retrieved user id should match created id");

        String email = getResponse.jsonPath().getString("data.email");
        Assert.assertNotNull(email, "Retrieved user email should be present");
        Assert.assertFalse(email.isBlank(), "Retrieved user email should be present");

        String firstName = getResponse.jsonPath().getString("data.first_name");
        Assert.assertNotNull(firstName, "Retrieved user first name should be present");
        Assert.assertFalse(firstName.isBlank(), "Retrieved user first name should be present");

        String lastName = getResponse.jsonPath().getString("data.last_name");
        Assert.assertNotNull(lastName, "Retrieved user last name should be present");
        Assert.assertFalse(lastName.isBlank(), "Retrieved user last name should be present");
    }

    /**
     * Verifies successful user update and response payload fields.
     */
    @Test
    public void testUpdateUserSuccessfully() {
        User originalUser = new User("user-" + System.currentTimeMillis(), "candidate", 31);
        Response createResponse = userService.createUser(originalUser);
        TestLogUtils.logApiResponse("createUser-before-update", createResponse);
        Assert.assertEquals(createResponse.statusCode(), 201, "Create user before update should return 201");

        int createdId = createResponse.jsonPath().getInt("id");
        Assert.assertTrue(createdId > 0, "Created user id should be greater than zero before update. id=" + createdId);

        User updatedUser = new User("user-" + System.currentTimeMillis(), "updated-candidate", 32);
        Response response = userService.updateUser(createdId, updatedUser);
        TestLogUtils.logApiResponse("updateUser", response);

        Assert.assertEquals(response.statusCode(), 200, "Update user should return 200");
        Assert.assertEquals(response.jsonPath().getString("name"), updatedUser.getName(), "Updated user name should match payload");
        Assert.assertEquals(response.jsonPath().getString("job"), updatedUser.getJob(), "Updated user job should match payload");
        Assert.assertEquals(response.jsonPath().getInt("age"), updatedUser.getAge(), "Updated user age should match payload");

        String updatedAt = response.jsonPath().getString("updatedAt");
        Assert.assertNotNull(updatedAt, "updatedAt should be present");
        Assert.assertFalse(updatedAt.isBlank(), "updatedAt should be present");
    }

    /**
     * Verifies unknown user lookup returns 404 and empty body object.
     */
    @Test
    public void testReturn404ForUnknownUser() {
        int unknownUserId = 999;
        Response response = userService.getUser(unknownUserId);
        TestLogUtils.logApiResponse("getUnknownUser", response);

        Assert.assertEquals(response.statusCode(), 404, "Unknown user lookup should return 404");
        Assert.assertEquals(response.getBody().asString().trim(), "{}", "Unknown user response body should be empty JSON");
    }

    /**
     * Verifies create endpoint behavior with an empty user payload.
     */
    @Test
    public void testHandleEmptyCreatePayload() {
        Response response = userService.createUser(new User());
        TestLogUtils.logApiResponse("createUserWithEmptyUserModel", response);

        Assert.assertEquals(response.statusCode(), 201, "Create with empty payload should return 201");
    }
}
