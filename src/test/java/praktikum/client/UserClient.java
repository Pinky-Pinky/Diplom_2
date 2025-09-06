package praktikum.client;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.qameta.allure.Step;
import praktikum.model.UserRequest;

public class UserClient extends BaseClient {

    @Step("Создать пользователя")
    public Response createUser(UserRequest user) {
        return RestAssured.given()
                .spec(getSpec())
                .body(user)
                .post("/api/auth/register");
    }

    @Step("Удалить пользователя")
    public Response deleteUser(String accessToken) {
        return RestAssured.given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .delete("/api/auth/user");
    }
}