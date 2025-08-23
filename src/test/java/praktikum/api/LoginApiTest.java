package praktikum.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class LoginApiTest {

    private String email;
    private String password;
    private String name;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";

        // Генерация уникального пользователя
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        email = "testuser_" + uniqueId + "@yandex.ru";
        password = "password123";
        name = "User" + uniqueId;

        // Регистрация пользователя
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("password", password);
        userData.put("name", name);

        given()
                .contentType(ContentType.JSON)
                .body(userData)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    public void loginWithValidUser_success() {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", email);
        loginData.put("password", password);

        given()
                .contentType(ContentType.JSON)
                .body(loginData)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name));
    }

    @Test
    public void loginWithInvalidPassword_failure() {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", email);
        loginData.put("password", "wrongPassword");

        given()
                .contentType(ContentType.JSON)
                .body(loginData)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
