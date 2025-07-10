package com.dummyjson.api.tests.tests;

import com.dummyjson.api.tests.dto.Product;
import com.dummyjson.api.tests.helpers.ConfigReader;
import com.dummyjson.api.tests.wrappers.AuthApiWrapper;
import com.dummyjson.api.tests.wrappers.ProductsApiWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Assertions;


public class ProductsTests extends TestBase {
    private ProductsApiWrapper productsApi;

    @BeforeEach
    void setUp() {
        AuthApiWrapper authApi = new AuthApiWrapper();
        // Выполняем логин для операций, требующих авторизации
        authApi.login(ConfigReader.getProperty("auth.username"), ConfigReader.getProperty("auth.password"));
        productsApi = new ProductsApiWrapper(authApi);
    }

    @Test
    @DisplayName("Get all products and verify count")
    void getAllProductsAndVerifyCount() {
        productsApi.getAllProducts()
                .then()
                .statusCode(200)
                .body(
                        "products", hasSize(30),
                        "total", equalTo(194),
                        "skip", equalTo(0),
                        "limit", equalTo(30)
                );
    }

    @Test
    @DisplayName("Get single product by ID - Positive test with Schema Validation")
    void getSingleProductByIdTest() {
        int productId = 1;
        productsApi.getProductById(productId)
                .then()
                .statusCode(200)
                .body(
                        "id", equalTo(productId),
                        "title", equalTo("Essence Mascara Lash Princess"),
                        "price", greaterThan(0F)
                )
                .body(matchesJsonSchemaInClasspath("schemas/product_schema.json"));
    }

    @Test
    @DisplayName("Create new product (Positive)")
    void createNewProductPositiveTest() {
        Product newProductRequest = new Product(
                "My Awesome New Product",
                "This is a fantastic product created via automation.",
                99.99F,
                100,
                "electronics",
                "MyBrand"
        );

        Product createdProductResponse = productsApi.createProduct(newProductRequest)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo(newProductRequest.getTitle()))
                .body("price", equalTo(newProductRequest.getPrice()))
                .extract()
                .as(Product.class);

        Assertions.assertNotNull(createdProductResponse.getId(), "Product ID should not be null");
        Assertions.assertEquals(newProductRequest.getTitle(), createdProductResponse.getTitle(), "Product title mismatch");
        Assertions.assertEquals(newProductRequest.getPrice(), createdProductResponse.getPrice(), 0.001F, "Product price mismatch");
        Assertions.assertEquals(newProductRequest.getCategory(), createdProductResponse.getCategory(), "Product category mismatch");

        System.out.println("Successfully created product with ID: " + createdProductResponse.getId() + " and title: " + createdProductResponse.getTitle());
        System.out.println("Full product details: " + createdProductResponse.toString());
    }

    @Test
    @DisplayName("Create new product (Negative Price Accepted)")
    void createNewProductNegativePriceTest() {

        Product newProductRequest = new Product(
                "Product with Negative Price Accepted",
                "This product is intentionally sent with a negative price.",
                -10.95F,
                100,
                "electronics",
                "NegativePriceBrand"
        );

        productsApi.createProduct(newProductRequest)
                .then()
                .statusCode(201)
                .body("price", equalTo(-10.95F));
        System.out.println("ALERT: DummyJSON API allowed creation of product with negative price.");
    }

    @Test
    @DisplayName("Update a product (Positive)")
    void updateProductPositiveTest() {
        int productIdToUpdate = 1;

        String updatedTitle = "Updated Essence Mascara Lash Princess";
        float updatedPrice = 19.45F;
        String updatedDescription = "This is the updated description for the product.";

        Product productUpdate = new Product();
        productUpdate.setTitle(updatedTitle);
        productUpdate.setPrice(updatedPrice);
        productUpdate.setDescription(updatedDescription);

        Product updatedProductResponse = productsApi.updateProduct(productIdToUpdate, productUpdate)
                .then()
                .statusCode(200)
                .body("id", equalTo(productIdToUpdate))
                .body("title", equalTo(updatedTitle))
                .extract()
                .as(Product.class);

        Assertions.assertEquals(productIdToUpdate, updatedProductResponse.getId(), "Product ID mismatch");
        Assertions.assertEquals(updatedTitle, updatedProductResponse.getTitle(), "Product title was not updated correctly");
        Assertions.assertEquals(updatedDescription, updatedProductResponse.getDescription(), "Product description was not updated correctly");

        System.out.println("Successfully updated product with ID: " + updatedProductResponse.getId() + " to new title: " + updatedProductResponse.getTitle());
        System.out.println("Full updated product details: " + updatedProductResponse.toString());
    }

    @Test
    @DisplayName("Update a product (Negative - Non-existent ID)")
    void updateProductNonExistentIdTest() {
        int nonExistentProductId = 99999;
        String titleForUpdate = "Attempt to Update Non-existent Product";
        float priceForUpdate = 5.00F;

        Product productUpdate = new Product();
        productUpdate.setTitle(titleForUpdate);
        productUpdate.setPrice(priceForUpdate);

        productsApi.updateProduct(nonExistentProductId, productUpdate)
                .then()
                .statusCode(404)
                .body("message", equalTo("Product with id '" + nonExistentProductId + "' not found"));
    }

    @Test
    @DisplayName("Delete a product (Positive)")
    void deleteProductPositiveTest() {
        int productIdToDelete = 1;

        Product deletedProductResponse = productsApi.deleteProduct(productIdToDelete)
                .then()
                .statusCode(200)
                .body(
                        "id", equalTo(productIdToDelete),
                        "isDeleted", equalTo(true),
                        "deletedOn", notNullValue())
                .extract()
                .as(Product.class);


        Assertions.assertEquals(productIdToDelete, deletedProductResponse.getId(), "Product ID mismatch");
        Assertions.assertTrue(deletedProductResponse.getIsDeleted(), "Product should be marked as deleted");
        Assertions.assertNotNull(deletedProductResponse.getDeletedOn(), "DeletedOn timestamp should not be null");

        System.out.println("Successfully marked product with ID: " + deletedProductResponse.getId() + " as deleted.");
        System.out.println("Full deleted product details: " + deletedProductResponse.toString());
    }

    @Test
    @DisplayName("Delete a product (Negative - Non-existent ID)")
    void deleteProductNegativeNonExistentIdTest() {
        int nonExistentProductId = 99999;

        productsApi.deleteProduct(nonExistentProductId)
                .then()
                .statusCode(404)
                .body("message", equalTo("Product with id '" + nonExistentProductId + "' not found"));
    }
}

