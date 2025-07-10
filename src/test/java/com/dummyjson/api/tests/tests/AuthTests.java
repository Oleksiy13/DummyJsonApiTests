package com.dummyjson.api.tests.tests;

import com.dummyjson.api.tests.dto.LoginRequest;
import com.dummyjson.api.tests.dto.LoginResponse;
import com.dummyjson.api.tests.dto.User;
import com.dummyjson.api.tests.wrappers.AuthApiWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Assertions;
import com.dummyjson.api.tests.helpers.ConfigReader;


public class AuthTests extends TestBase {

    private AuthApiWrapper authApi;

    @BeforeEach
    void setUp() {
        authApi = new AuthApiWrapper();
    }

    @Test
    @DisplayName("Successful user login with valid credentials")
    void successfulUserLoginTest() {

        String username = ConfigReader.getProperty("auth.username");
        String password = ConfigReader.getProperty("auth.password");

        LoginResponse loginResponse = authApi.login(username, password);

        System.out.println("Received token: " + loginResponse.getAccessToken());
        System.out.println("Received user name: " + loginResponse.getUsername());
        Assertions.assertEquals(username, loginResponse.getUsername(), "Usernames do not match");

        authToken = loginResponse.getAccessToken();
    }

    @Test
    @DisplayName("Get logged-in user data using valid token with Schema Validation")
    void getLoggedInUserDataTest() {

        String username = ConfigReader.getProperty("auth.username");
        String password = ConfigReader.getProperty("auth.password");
        authApi.login(username, password);

        User userResponse = authApi.getCurrentUser()
                .then()
                .statusCode(200)
                .body("username", equalTo(username))
                .body("email", not(emptyOrNullString()))
                .body(matchesJsonSchemaInClasspath("schemas/user_schema.json"))
                .extract()
                .as(User.class);

        System.out.println("User data received: " + userResponse.toString());
        Assertions.assertEquals(username, userResponse.getUsername(),
                "Username in /auth/me response should match logged in user.");
    }

    @Test
    @DisplayName("Login with invalid credentials - Negative test")
    void loginWithInvalidCredentialsTest() {
        LoginRequest invalidLoginRequest = new LoginRequest("invalidUser", "wrongPassword");

        authApi.postLogin(invalidLoginRequest)
                .then()
                .statusCode(400)
                .body("message", equalTo("Invalid credentials"));
    }

    @Test
    @DisplayName("Login with missing username - Negative test")
    void loginWithMissingUsernameTest() {
        LoginRequest loginRequest = new LoginRequest(null, ConfigReader.getProperty("auth.password"));

        authApi.postLogin(loginRequest)
                .then()
                .statusCode(400)
                .body("message", equalTo("Username and password required"));
    }

    @Test
    @DisplayName("Login with missing password - Negative test")
    void loginWithMissingPasswordTest() {
        LoginRequest loginRequest = new LoginRequest(ConfigReader.getProperty("auth.username"), null);

        authApi.postLogin(loginRequest)
                .then()
                .statusCode(400)
                .body("message", equalTo("Username and password required"));
    }
}

