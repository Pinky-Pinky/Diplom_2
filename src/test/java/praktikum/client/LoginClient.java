package praktikum.client;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import praktikum.model.LoginRequest;

public class LoginClient extends BaseClient {

    public Response login(LoginRequest login) {
        return RestAssured.given()
                .spec(getSpec())
                .body(login)
                .post("/api/auth/login");
    }
}