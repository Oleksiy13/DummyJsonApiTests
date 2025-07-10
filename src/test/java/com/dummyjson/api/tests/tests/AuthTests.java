package com.dummyjson.api.tests.tests;

import com.dummyjson.api.tests.dto.LoginRequest;
import com.dummyjson.api.tests.dto.LoginResponse;
import com.dummyjson.api.tests.dto.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Assertions;
import com.dummyjson.api.tests.helpers.ConfigReader;


public class AuthTests extends TestBase {

    @Test
    @DisplayName("Successful user login with valid credentials")
    void successfulUserLoginTest() {

        String username = ConfigReader.getProperty("auth.username");
        String password = ConfigReader.getProperty("auth.password");

        LoginRequest loginRequest = new LoginRequest(username, password);

        LoginResponse loginResponse = given()
                .body(loginRequest)
                .when()
                .post("auth/login")
                .then()
                .statusCode(200)
                .body("accessToken", not(emptyOrNullString()))
                .extract()
                .as(LoginResponse.class);

        System.out.println("Received token: " + loginResponse.getAccessToken());
        System.out.println("Received user name: " + loginResponse.getUsername());
        Assertions.assertEquals(username, loginResponse.getUsername(), "Usernames do not match");

        authToken = loginResponse.getAccessToken();
    }

    @Test
    @DisplayName("Get logged-in user data using valid token with Schema Validation")
    void getLoggedInUserDataTest() {

        if (authToken == null || authToken.isEmpty()) {
            System.out.println("Auth token is null or empty. Running successfulUserLoginTest to obtain it.");
            successfulUserLoginTest();
        }

        Assertions.assertNotNull(authToken, "Auth token should not be null after login attempt.");
        Assertions.assertFalse(authToken.isEmpty(), "Auth token should not be empty after login attempt.");

        User userResponse = given()

                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/auth/me")
                .then()
                .statusCode(200)
                .body("username", equalTo(ConfigReader.getProperty("auth.username")))
                .body("email", not(emptyOrNullString()))
                .body(matchesJsonSchemaInClasspath("schemas/user_schema.json"))
                .extract()
                .as(User.class);

        System.out.println("User data received: " + userResponse.toString());
        Assertions.assertEquals(ConfigReader.getProperty("auth.username"), userResponse.getUsername(), "Username in /auth/me response should match logged in user.");
    }

    @Test
    @DisplayName("Login with invalid credentials - Negative test")
    void loginWithInvalidCredentialsTest() {
        LoginRequest invalidLoginRequest = new LoginRequest("invalidUser", "wrongPassword");

        given()
                .body(invalidLoginRequest)
                .when()
                .post("auth/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Invalid credentials"));
    }

    @Test
    @DisplayName("Login with missing username - Negative test")
    void loginWithMissingUsernameTest() {
        LoginRequest loginRequest = new LoginRequest(null, ConfigReader.getProperty("auth.password"));

        given()
                .body(loginRequest)
                .when()
                .post("auth/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Username and password required"));
    }

    @Test
    @DisplayName("Login with missing password - Negative test")
    void loginWithMissingPasswordTest() {
        LoginRequest loginRequest = new LoginRequest(ConfigReader.getProperty("auth.username"), null);

        given()
                .body(loginRequest)
                .when()
                .post("auth/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Username and password required"));
    }
}

