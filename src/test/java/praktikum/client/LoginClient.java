package praktikum.client;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.qameta.allure.Step;
import praktikum.model.LoginRequest;

public class LoginClient extends BaseClient {

    @Step("Выполнить логин пользователя")
    public Response login(LoginRequest login) {
        return RestAssured.given()
                .spec(getSpec())
                .body(login)
                .post("/api/auth/login");
    }
}