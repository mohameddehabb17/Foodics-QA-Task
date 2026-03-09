package com.foodics.qa.api.client;

import com.foodics.qa.api.models.User;
import io.restassured.response.Response;

import java.util.logging.Level;
import java.util.logging.Logger;

import static io.restassured.RestAssured.given;

/**
 * Service client for user-related ReqRes endpoints.
 */
public class UserService extends BaseApiClient {
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
    private static final String USERS_PATH = "/api/users";

    /**
     * Creates a new user resource.
     *
     * @param user request payload
     * @return API response
     */
    public Response createUser(User user) {
        LOGGER.log(Level.FINE, "Creating user with path {0}", USERS_PATH);
        return given()
                .spec(requestSpec())
                .body(user)
                .when()
                .post(USERS_PATH)
                .then()
                .log().ifError()
                .extract().response();
    }

    /**
     * Retrieves user details by identifier.
     *
     * @param id user ID
     * @return API response
     */
    public Response getUser(int id) {
        LOGGER.log(Level.FINE, "Fetching user by id={0}", id);
        return given()
                .spec(requestSpec())
                .pathParam("id", id)
                .when()
                .get(USERS_PATH + "/{id}")
                .then()
                .log().ifError()
                .extract().response();
    }

    /**
     * Updates existing user details by identifier.
     *
     * @param id user ID
     * @param user request payload
     * @return API response
     */
    public Response updateUser(int id, User user) {
        LOGGER.log(Level.FINE, "Updating user id={0}", id);
        return given()
                .spec(requestSpec())
                .pathParam("id", id)
                .body(user)
                .when()
                .put(USERS_PATH + "/{id}")
                .then()
                .log().ifError()
                .extract().response();
    }
}

