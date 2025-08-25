package praktikum.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderApiTest {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String ORDER_ENDPOINT = "/api/orders";

    // существующий пользователь
    private static final String EMAIL = "pinky-pinky@yandex.ru";
    private static final String PASSWORD = "password";

    // валидные id ингредиентов (булка + начинка)
    private static final List<String> VALID_INGREDIENTS = Arrays.asList(
            "61c0c5a71d1f82001bdaaa6d", // Флюоресцентная булка R2-D3
            "61c0c5a71d1f82001bdaaa6f"  // Мясо бессмертных моллюсков Protostomia
    );

    // заведомо несуществующий id правильной длины
    private static final List<String> INVALID_INGREDIENTS = Collections.singletonList(
            "1234567890abcdef12345678"
    );

    private String accessToken; // приходит уже с префиксом "Bearer "

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;

        String loginBody = "{ \"email\": \"" + EMAIL + "\", \"password\": \"" + PASSWORD + "\" }";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(loginBody)
                .when()
                .post(LOGIN_ENDPOINT)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .extract().response();

        accessToken = response.path("accessToken");
    }

    /**
     * 1) Создание заказа с авторизацией + с ингредиентами -> 200 OK, success=true
     */
    @Test
    public void createOrderWithAuthAndIngredients_success() {
        given()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body(Collections.singletonMap("ingredients", VALID_INGREDIENTS))
                .when()
                .post(ORDER_ENDPOINT)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }

    /**
     * 2) Создание заказа без авторизации (но с ингредиентами)
     * Фактическое поведение API: заказ создаётся -> 200 OK, success=true
     */
    @Test
    public void createOrderWithIngredientsWithoutAuth_success() {
        given()
                .contentType(ContentType.JSON)
                .body(Collections.singletonMap("ingredients", VALID_INGREDIENTS))
                .when()
                .post(ORDER_ENDPOINT)
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }

    /**
     * 3) Создание заказа с авторизацией, но без ингредиентов
     * Ожидаем валидационную ошибку -> 400 Bad Request
     */
    @Test
    public void createOrderWithAuthButNoIngredients_fail400() {
        given()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body(Collections.singletonMap("ingredients", Collections.emptyList()))
                .when()
                .post(ORDER_ENDPOINT)
                .then()
                .statusCode(400)
                .body("success", is(false))
                .body("message", anyOf(
                        equalTo("Ingredient ids must be provided"),
                        containsString("must be provided")
                ));
    }

    /**
     * 4) Создание заказа без авторизации и без ингредиентов
     * Фактическое поведение API: валидация срабатывает раньше авторизации -> 400 Bad Request
     */
    @Test
    public void createOrderNoIngredientsNoAuth_fail400() {
        given()
                .contentType(ContentType.JSON)
                .body(Collections.singletonMap("ingredients", Collections.emptyList()))
                .when()
                .post(ORDER_ENDPOINT)
                .then()
                .statusCode(400)
                .body("success", is(false))
                .body("message", anyOf(
                        equalTo("Ingredient ids must be provided"),
                        containsString("must be provided")
                ));
    }

    /**
     * 5) Создание заказа с неверным хешем ингредиента
     * Фактическое поведение API: 400 Bad Request, success=false, сообщение про некорректные id.
     */
    @Test
    public void createOrderWithInvalidIngredients_fail400() {
        given()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body(Collections.singletonMap("ingredients", INVALID_INGREDIENTS))
                .when()
                .post(ORDER_ENDPOINT)
                .then()
                .statusCode(400)
                .body("success", is(false))
                .body("message", anyOf(
                        equalTo("One or more ids provided are incorrect"),
                        containsString("ids"),
                        containsString("incorrect")
                ));
    }
}
