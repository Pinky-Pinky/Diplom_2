package praktikum.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserApiTest {

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    public void createUniqueUser_success() {
        String uniqueEmail = "test" + System.currentTimeMillis() + "@yandex.ru";

        given()
                .contentType(ContentType.JSON)
                .body("{\"email\": \"" + uniqueEmail + "\", \"password\": \"password\", \"name\": \"Username\"}")
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(uniqueEmail));
    }

    @Test
    public void createExistingUser_fail() {
        String existingEmail = "test@yandex.ru"; // укажи реально существующего пользователя

        given()
                .contentType(ContentType.JSON)
                .body("{\"email\": \"" + existingEmail + "\", \"password\": \"password\", \"name\": \"Username\"}")
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    public void createUserWithoutRequiredField_fail() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"email\": \"\", \"password\": \"password\", \"name\": \"\"}")
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
