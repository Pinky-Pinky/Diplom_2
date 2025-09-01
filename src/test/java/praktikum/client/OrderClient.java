package praktikum.client;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import praktikum.model.OrderRequest;

public class OrderClient extends BaseClient {

    public Response createOrder(OrderRequest order, String accessToken) {
        return RestAssured.given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .body(order)
                .post("/api/orders");
    }

    public Response createOrderWithoutAuth(OrderRequest order) {
        return RestAssured.given()
                .spec(getSpec())
                .body(order)
                .post("/api/orders");
    }
}