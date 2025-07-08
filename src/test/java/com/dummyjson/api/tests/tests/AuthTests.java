package com.dummyjson.api.tests.tests;

import com.dummyjson.api.tests.dto.LoginRequest;
import com.dummyjson.api.tests.dto.LoginResponse;
import com.dummyjson.api.tests.dto.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Assertions;


public class AuthTests extends TestBase {

    @Test
    @DisplayName("Successful user login with valid credentials")
    void successfulUserLoginTest() {
        String username = properties.getProperty("auth.username");
        String password = properties.getProperty("auth.password");

        LoginRequest loginRequest = new LoginRequest(username, password);

        LoginResponse loginResponse = given()
                .header("Content-Type", "application/json")
                .body(loginRequest)
                .when()
                .post("auth/login")
                .then()
                .log().all()
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
    @DisplayName("Get logged-in user data using valid token")
    void getLoggedInUserDataTest() {

        if (authToken == null) {
            System.out.println("Auth token is null. Running successfulUserLoginTest to obtain it.");
            successfulUserLoginTest();
        }

        Assertions.assertNotNull(authToken, "Auth token should not be null after login attempt.");
        Assertions.assertFalse(authToken.isEmpty(), "Auth token should not be empty after login attempt.");

        User userResponse = given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/auth/me")
                .then()
                .log().all()
                .statusCode(200)
                .body("username", equalTo(properties.getProperty("auth.username")))
                .body("email", not(emptyOrNullString()))
                .extract()
                .as(User.class);

        System.out.println("User data received: " + userResponse.toString());
        Assertions.assertEquals(properties.getProperty("auth.username"), userResponse.getUsername(), "Username in /auth/me response should match logged in user.");

    }
}

