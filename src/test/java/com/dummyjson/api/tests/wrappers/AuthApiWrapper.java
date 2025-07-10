package com.dummyjson.api.tests.wrappers;

import com.dummyjson.api.tests.dto.LoginRequest;
import com.dummyjson.api.tests.dto.LoginResponse;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthApiWrapper {
    private String authToken;

    public LoginResponse login(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        LoginResponse response = given()
                .body(loginRequest)
                .post("auth/login")
                .then()
                .extract()
                .as(LoginResponse.class);
        this.authToken = response.getAccessToken();
        return response;
    }


    public String getAuthToken() {
        return authToken;
    }


    public Response getCurrentUser() {
        return given()
                .header("Authorization", "Bearer " + authToken)
                .get("/auth/me");
    }


    public Response postLogin(LoginRequest loginRequest) {
        return given()
                .body(loginRequest)
                .post("auth/login");
    }
}