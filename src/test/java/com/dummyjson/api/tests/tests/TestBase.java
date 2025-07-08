package com.dummyjson.api.tests.tests;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestBase {


    protected static Properties properties;
    protected static String authToken;

    @BeforeAll
    public static void setup() {
        properties = new Properties();
        try (InputStream input = TestBase.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("ERROR: Unable to find config.properties in classpath. Please ensure it's in src/test/resources.");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("ERROR loading config.properties: " + ex.getMessage(), ex);
        }


        RestAssured.baseURI = properties.getProperty("base.uri");


        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }
}