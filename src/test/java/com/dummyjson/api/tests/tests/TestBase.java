package com.dummyjson.api.tests.tests;

import com.dummyjson.api.tests.helpers.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import static com.dummyjson.api.tests.helpers.CustomAllureListener.withCustomTemplates;


public class TestBase {

    protected static String authToken;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = ConfigReader.getProperty("base.uri");

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), withCustomTemplates());
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

    }
}