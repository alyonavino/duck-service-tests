package clients;

import autotests.EndpointConfig;
import autotests.Payloads.Duck;
import autotests.Payloads.WingState;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.dsl.JsonPathSupport;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.message.builder.ObjectMappingPayloadBuilder;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.atomic.AtomicInteger;

import static com.consol.citrus.DefaultTestActionBuilder.action;
import static com.consol.citrus.actions.ExecuteSQLAction.Builder.sql;
import static com.consol.citrus.actions.ExecuteSQLQueryAction.Builder.query;
import static com.consol.citrus.dsl.JsonPathSupport.jsonPath;
import static com.consol.citrus.dsl.MessageSupport.MessageBodySupport.fromBody;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

@ContextConfiguration(classes = {EndpointConfig.class})
public class DuckActionClient extends TestNGCitrusSpringSupport {
    @Autowired
    protected HttpClient duckService;
    @Autowired
    protected SingleConnectionDataSource testDb;

    @Step("Обновление базы данных")
    public void databaseUpdate(TestCaseRunner runner, String sql) {
        runner.$(sql(testDb)
                .statement(sql));
    }

    @Step("Проверяем уточку в базе данных")
    protected void validateDuckInDatabase(TestCaseRunner runner, String id, String color, String height, String material, String sound, String wingsState) {
        runner.$(query(testDb)
                .statement("SELECT * FROM DUCK WHERE ID=" + id)
                .validate("COLOR", color)
                .validate("HEIGHT", height)
                .validate("MATERIAL", material)
                .validate("SOUND", sound)
                .validate("WINGS_STATE", wingsState));
    }

    @Step("Удаление уточки из базы данных")
    public void databaseDelete(TestCaseRunner runner, String id) {
        runner.$(sql(testDb)
                .statement("DELETE FROM DUCK WHERE ID=" + id));
    }

    @Step("Создание уточки в базе данных")
   public void createDuckInBd(TestCaseRunner runner, String sql) {
        runner.$(sql(testDb).statement(sql));
   }

    //    создание уточки
    @Step("Эндпоинт для создания уточки")
    public void createDuck(TestCaseRunner runner, Object body) {
        runner.$(http().client(duckService)
                .send()
                .post("/api/duck/create")
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new ObjectMappingPayloadBuilder(body, new ObjectMapper())));
    }

    // удаление уточки
    @Step("Эндпоинт для удаления уточки")
    public void duckDelete(TestCaseRunner runner) {
        runner.$(http().client(duckService)
                .send()
                .delete("/api/duck/delete")
                .queryParam("id", "${id}"));
    }

    // обновление свойств уточки
    @Step("Эндпоинт для обновления свойств уточки")
    public void duckUpdate(TestCaseRunner runner, String color, double height, String material, String sound, WingState wingsState) {
        runner.$(http().client(duckService)
                .send()
                .put("/api/duck/update")
                .queryParam("color", color)
                .queryParam("height", String.valueOf(height))
                .queryParam("material", material)
                .queryParam("sound", sound)
                .queryParam("wingsState", String.valueOf(wingsState))
                .queryParam("id", "${id}"));
    }

    // заставить уточку полетать
    @Step("Эндпоинт для того, чтобы заставить уточку полетать")
    public void duckFly(TestCaseRunner runner, String id) {
        runner.$(http().client(duckService)
                .send()
                .get("/api/duck/action/fly")
                .queryParam("id", id));
    }

    // заставить уточку крякать
    @Step("Эндпоинт для того, чтобы заставить уточку крякать")
    public void duckQuack(TestCaseRunner runner, String id, int repetitionCount, int soundCount) {
        runner.$(http().client(duckService)
                .send()
                .get("/api/duck/action/quack")
                .queryParam("id", id)
                .queryParam("repetitionCount", String.valueOf(repetitionCount))
                .queryParam("soundCount", String.valueOf(soundCount)));
    }

    // заставить уточку плавать
    @Step("Эндпоинт для того, чтобы заставить уточку плавать")
    public void duckSwim(TestCaseRunner runner, String id) {
        runner.$(http().client(duckService)
                .send()
                .get("/api/duck/action/swim")
                .queryParam("id", id));
    }

    // получение свойств уточки
    @Step("Эндпоинт для того, чтобы получить свойства уточки")
    public void duckProperties(TestCaseRunner runner, String id) {
        runner.$(http().client(duckService)
                .send()
                .get("/api/duck/action/properties")
                .queryParam("id", id));
    }

    @Step("Валидация с передачей ответа String'ой")
    public void validateResponse(TestCaseRunner runner, String expectedMessage, HttpStatus httpStatus) {
        runner.$(http().client(duckService)
                .receive()
                .response(httpStatus)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(expectedMessage));
    }

    @Step("Валидация с передачей ответа из папки resources")
    public void validateResponseFromResources(TestCaseRunner runner, String expectedPayload, HttpStatus httpStatus) {
        runner.$(http().client(duckService)
                .receive()
                .response(httpStatus)
                .message().type(MessageType.JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new ClassPathResource(expectedPayload)));
    }

    @Step("Валидация с передачей ответа из папки Payloads")
    public void validateResponseFromPayloads(TestCaseRunner runner, Object expectedPayload, HttpStatus httpStatus) {
        runner.$(http().client(duckService)
                .receive()
                .response(httpStatus)
                .message().type(MessageType.JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new ObjectMappingPayloadBuilder(expectedPayload, new ObjectMapper())));
    }

    @Step("Проверка все ли свойства такие, какие нужны по задаче")
    public void validateProperties(TestCaseRunner runner, String color, String height, String material, String sound, String wingsState) {
        runner.$(http().client(duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .validate(JsonPathSupport.jsonPath().expression("$.color", color))
                .validate(JsonPathSupport.jsonPath().expression("$.height", height))
                .validate(JsonPathSupport.jsonPath().expression("$.material", material))
                .validate(JsonPathSupport.jsonPath().expression("$.sound", sound))
                .validate(JsonPathSupport.jsonPath().expression("$.wingsState", wingsState)));
    }

    @Step("Извлечение id для всех методов")
    public AtomicInteger extractId(TestCaseRunner runner) {
        AtomicInteger id = new AtomicInteger();
        runner.$(http().client(duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .extract(fromBody().expression("$.id", "duckId")));
        runner.$(action(context -> {
            id.set(context.getVariable("${duckId}", int.class));
        }));
        return id;
    }

    @Step("Получение рандомного id (условие true = для четного id, условие false = для нечетного id")
    public AtomicInteger getRandomId(TestCaseRunner runner, boolean isEven) {
        AtomicInteger id = new AtomicInteger();
        do {
            runner.variable("id", "citrus:randomNumber(3,true)");
            runner.$(action(context -> {
                id.set(context.getVariable("${id}", int.class));
            }));
        } while (isEven ? id.get() % 2 != 0 : id.get() % 2 == 0);
        return id;
    }

    @Step("Извлечение id и проверка ответа")
    public void validateCreateAndGetId(TestCaseRunner runner, String responseMessage) {
        runner.$(http().client(duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .extract(fromBody().expression("$.id", "duckId"))
                .body(new ClassPathResource(responseMessage)));
    }
}
