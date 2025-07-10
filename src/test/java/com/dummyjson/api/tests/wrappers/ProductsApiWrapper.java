package com.dummyjson.api.tests.wrappers;

import com.dummyjson.api.tests.dto.Product;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ProductsApiWrapper {
    private final AuthApiWrapper authApi;

    public ProductsApiWrapper(AuthApiWrapper authApi) {
        this.authApi = authApi;
    }


    public Response getAllProducts() {
        return given().get("/products");
    }


    public Response getProductById(int productId) {
        return given().get("/products/" + productId);
    }


    public Response createProduct(Product product) {
        return given()
                .header("Authorization", "Bearer " + authApi.getAuthToken())
                .body(product)
                .post("/products/add");
    }


    public Response updateProduct(int productId, Product product) {
        return given()
                .header("Authorization", "Bearer " + authApi.getAuthToken())
                .body(product)
                .put("/products/" + productId);
    }


    public Response deleteProduct(int productId) {
        return given()
                .header("Authorization", "Bearer " + authApi.getAuthToken())
                .delete("/products/" + productId);
    }
}