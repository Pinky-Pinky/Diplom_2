package praktikum.api;

import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass; // Добавлен импорт
import org.junit.Test;
import praktikum.client.UserClient;
import praktikum.model.UserRequest;
import com.github.javafaker.Faker;

import static org.hamcrest.Matchers.equalTo;

public class UserApiTest {

    private static UserClient userClient = new UserClient();
    private static Faker faker = new Faker();
    private String email;
    private String accessToken;

    @BeforeClass
    public static void setup() {
        // Настройка base URI уже в UserClient через BaseClient
    }

    @Before
    public void setUp() {
        email = faker.internet().emailAddress();
        String password = faker.internet().password();
        String name = faker.name().username();

        UserRequest user = new UserRequest(email, password, name);
        Response response = userClient.createUser(user);
        response.then().statusCode(200).body("success", equalTo(true));
        accessToken = response.path("accessToken");
    }

    @Test
    public void createUniqueUser_success() {
        String uniqueEmail = faker.internet().emailAddress();
        UserRequest user = new UserRequest(uniqueEmail, faker.internet().password(), faker.name().username());
        Response response = userClient.createUser(user);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(uniqueEmail));
    }

    @Test
    public void createExistingUser_fail() {
        UserRequest user = new UserRequest(email, faker.internet().password(), faker.name().username());
        Response response = userClient.createUser(user);
        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    public void createUserWithoutEmail_fail() {
        UserRequest user = new UserRequest("", faker.internet().password(), faker.name().username());
        Response response = userClient.createUser(user);
        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    public void createUserWithoutPassword_fail() {
        UserRequest user = new UserRequest(faker.internet().emailAddress(), "", faker.name().username());
        Response response = userClient.createUser(user);
        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    public void createUserWithoutName_fail() {
        UserRequest user = new UserRequest(faker.internet().emailAddress(), faker.internet().password(), "");
        Response response = userClient.createUser(user);
        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
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