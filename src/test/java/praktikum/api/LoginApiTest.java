package praktikum.api;

import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.client.LoginClient;
import praktikum.client.UserClient;
import praktikum.model.LoginRequest;
import praktikum.model.UserRequest;
import com.github.javafaker.Faker;

import static org.hamcrest.Matchers.equalTo;

public class LoginApiTest {

    private UserClient userClient = new UserClient();
    private LoginClient loginClient = new LoginClient();
    private Faker faker = new Faker();

    private String email;
    private String password;
    private String name;
    private String accessToken;

    @Before
    public void setUp() {
        email = faker.internet().emailAddress();
        password = faker.internet().password();
        name = faker.name().username();

        UserRequest user = new UserRequest(email, password, name);
        Response createResponse = userClient.createUser(user);
        createResponse.then().statusCode(200).body("success", equalTo(true));
        accessToken = createResponse.path("accessToken");
        // Извлекаем name из ответа, если он возвращается (зависит от API)
        // Если name не возвращается, используем сгенерированный выше
    }

    @Test
    public void loginWithValidUser_success() {
        LoginRequest login = new LoginRequest(email, password);
        Response response = loginClient.login(login);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name)); // Используем сгенерированный name
    }

    @Test
    public void loginWithInvalidPassword_failure() {
        LoginRequest login = new LoginRequest(email, "wrongPassword");
        Response response = loginClient.login(login);
        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
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