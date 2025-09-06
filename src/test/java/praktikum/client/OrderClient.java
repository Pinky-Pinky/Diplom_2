package praktikum.client;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.qameta.allure.Step;
import praktikum.model.OrderRequest;

public class OrderClient extends BaseClient {

    @Step("Создать заказ с авторизацией")
    public Response createOrder(OrderRequest order, String accessToken) {
        return RestAssured.given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .body(order)
                .post("/api/orders");
    }

    @Step("Создать заказ без авторизации")
    public Response createOrderWithoutAuth(OrderRequest order) {
        return RestAssured.given()
                .spec(getSpec())
                .body(order)
                .post("/api/orders");
    }
}
