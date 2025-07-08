package com.dummyjson.api.tests.tests;

import com.dummyjson.api.tests.dto.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;


public class ProductsTests extends TestBase {

    @Test
    @DisplayName("Get all products and verify count")
    void getAllProductsAndVerifyCount() {
        given()
                .when()
                .get("/products")
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
    @DisplayName("Get single product by ID - Positive test")
    void getSingleProductByIdTest() {
        int productId = 1;
        given()
                .when()
                .get("/products/" + productId)
                .then()
                .log().all()
                .statusCode(200)
                .body(
                        "id", equalTo(productId),
                        "title", equalTo("Essence Mascara Lash Princess"),
                        "price", greaterThan(0F)
                );
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

        Product createdProductResponse = given()
                .header("Content-Type", "application/json")
                .body(newProductRequest)
                .when()
                .post("/products/add")
                .then()
                .log().all()
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


        given()
                .header("Content-Type", "application/json")
                .body(newProductRequest)
                .when()
                .post("/products/add")
                .then()
                .log().all()
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

        Product updatedProductResponse = given()
                .header("Content-Type", "application/json")
                .body(productUpdate)
                .when()
                .put("/products/" + productIdToUpdate)
                .then()
                .log().all()
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
        int nonExistentProductId = 99999; // ID, которого точно нет
        String titleForUpdate = "Attempt to Update Non-existent Product";
        float priceForUpdate = 5.00F;

        Product productUpdate = new Product();
        productUpdate.setTitle(titleForUpdate);
        productUpdate.setPrice(priceForUpdate);

        given()
                .header("Content-Type", "application/json")
                .body(productUpdate)
                .when()
                .put("/products/" + nonExistentProductId)
                .then()
                .log().all()
                .statusCode(404)
                .body("message", equalTo("Product with id '" + nonExistentProductId + "' not found"));
    }
}

