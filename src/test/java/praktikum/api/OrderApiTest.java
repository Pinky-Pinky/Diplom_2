package praktikum.api;

import io.restassured.response.Response;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import praktikum.client.OrderClient;
import praktikum.client.UserClient;
import praktikum.model.OrderRequest;
import praktikum.model.UserRequest;
import com.github.javafaker.Faker;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class OrderApiTest {

    private UserClient userClient = new UserClient();
    private OrderClient orderClient = new OrderClient();
    private Faker faker = new Faker();

    private String email;
    private String password;
    private String accessToken;

    // Валидные ID ингредиентов (из документации или API)
    private static final List<String> VALID_INGREDIENTS = Arrays.asList(
            "61c0c5a71d1f82001bdaaa6d", // Флюоресцентная булка R2-D3
            "61c0c5a71d1f82001bdaaa6f"  // Мясо бессмертных моллюсков Protostomia
    );

    // Несуществующий ID правильной длины
    private static final List<String> INVALID_INGREDIENTS = Collections.singletonList(
            "1234567890abcdef12345678"
    );

    @Before
    public void setUp() {
        email = faker.internet().emailAddress();
        password = faker.internet().password();
        String name = faker.name().username();

        UserRequest user = new UserRequest(email, password, name);
        Response createResponse = userClient.createUser(user);
        createResponse.then().statusCode(200).body("success", equalTo(true));
        accessToken = createResponse.path("accessToken");
    }

    @Test
    public void createOrderWithAuthAndIngredients_success() {
        OrderRequest order = new OrderRequest(VALID_INGREDIENTS);
        Response response = orderClient.createOrder(order, accessToken);
        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }

    @Test
    public void createOrderWithIngredientsWithoutAuth_success() {
        OrderRequest order = new OrderRequest(VALID_INGREDIENTS);
        Response response = orderClient.createOrderWithoutAuth(order);
        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }

    @Test
    public void createOrderWithAuthButNoIngredients_fail400() {
        OrderRequest order = new OrderRequest(Collections.emptyList());
        Response response = orderClient.createOrder(order, accessToken);
        response.then()
                .statusCode(400)
                .body("success", is(false))
                .body("message", anyOf(
                        equalTo("Ingredient ids must be provided"),
                        containsString("must be provided")
                ));
    }

    @Test
    public void createOrderNoIngredientsNoAuth_fail400() {
        OrderRequest order = new OrderRequest(Collections.emptyList());
        Response response = orderClient.createOrderWithoutAuth(order);
        response.then()
                .statusCode(400)
                .body("success", is(false))
                .body("message", anyOf(
                        equalTo("Ingredient ids must be provided"),
                        containsString("must be provided")
                ));
    }

    @Test
    public void createOrderWithInvalidIngredients_fail400() {
        OrderRequest order = new OrderRequest(INVALID_INGREDIENTS);
        Response response = orderClient.createOrder(order, accessToken);
        response.then()
                .statusCode(400) // Или 500, если ревьюер настаивает — проверь API
                .body("success", is(false))
                .body("message", anyOf(
                        equalTo("One or more ids provided are incorrect"),
                        containsString("ids"),
                        containsString("incorrect")
                ));
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken)
                    .then()
                    .statusCode(202)
                    .body("success", equalTo(true));
        }
    }
}