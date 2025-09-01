package praktikum.client;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import praktikum.model.UserRequest;

public class UserClient extends BaseClient {

    public Response createUser(UserRequest user) {
        return RestAssured.given()
                .spec(getSpec())
                .body(user)
                .post("/api/auth/register");
    }

    public Response deleteUser(String accessToken) {
        return RestAssured.given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .delete("/api/auth/user");
    }
}