package com.foodics.qa.api.client;

import com.foodics.qa.shared.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base API client that provides shared request specification setup.
 */
public class BaseApiClient {
    private static final Logger LOGGER = Logger.getLogger(BaseApiClient.class.getName());
    private static final String API_BASE_URL_KEY = "api.base.url";
    private static final String REQRES_API_KEY = "reqres.api.key";
    private static final String REQRES_HOST = "reqres.in";

    /**
     * Builds a reusable request specification.
     *
     * @return configured request specification
     * @throws IllegalStateException when API base URI is not configured
     */
    protected RequestSpecification requestSpec() {
        String configuredBaseUri = ConfigManager.get(API_BASE_URL_KEY);
        String baseUri = RestAssured.baseURI != null && !RestAssured.baseURI.isBlank()
                ? RestAssured.baseURI.trim()
                : configuredBaseUri == null ? null : configuredBaseUri.trim();
        String reqresApiKey = ConfigManager.get(REQRES_API_KEY);

        if (baseUri == null || baseUri.isBlank()) {
            throw new IllegalStateException("API base URI is not configured. Set " + API_BASE_URL_KEY);
        }

        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .log(LogDetail.METHOD)
                .log(LogDetail.URI);

        if (baseUri.contains(REQRES_HOST) && reqresApiKey != null && !reqresApiKey.isBlank()) {
            builder.addHeader("x-api-key", reqresApiKey.trim());
            LOGGER.log(Level.FINE, "Applied ReqRes API key header for host {0}", REQRES_HOST);
        }

        return builder.build();
    }
}

