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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.atomic.AtomicInteger;

import static com.consol.citrus.DefaultTestActionBuilder.action;
import static com.consol.citrus.dsl.JsonPathSupport.jsonPath;
import static com.consol.citrus.dsl.MessageSupport.MessageBodySupport.fromBody;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

@ContextConfiguration(classes = {EndpointConfig.class})
public class DuckActionClient extends TestNGCitrusSpringSupport {
    @Autowired
    protected HttpClient duckService;

    //    создание уточки
    public void createDuck(TestCaseRunner runner, Object body) {
        runner.$(http().client(duckService)
                .send()
                .post("/api/duck/create")
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new ObjectMappingPayloadBuilder(body, new ObjectMapper())));
    }

    // удаление уточки
    public void duckDelete(TestCaseRunner runner, String id) {
        runner.$(http().client(duckService)
                .send()
                .delete("/api/duck/delete")
                .queryParam("id", id));
    }

    // обновление свойств уточки
    public void duckUpdate(TestCaseRunner runner, String color, double height, String id, String material, String sound, String wingsState) {
        runner.$(http().client(duckService)
                .send()
                .put("/api/duck/update")
                .queryParam("color", color)
                .queryParam("height", String.valueOf(height))
                .queryParam("id", id)
                .queryParam("material", material)
                .queryParam("sound", sound)
                .queryParam("wingsState", wingsState));
    }

    // заставить уточку полетать
    public void duckFly(TestCaseRunner runner, String id) {
        runner.$(http().client(duckService)
                .send()
                .get("/api/duck/action/fly")
                .queryParam("id", id));
    }

    // заставить уточку крякать
    public void duckQuack(TestCaseRunner runner, String id, int repetitionCount, int soundCount) {
        runner.$(http().client(duckService)
                .send()
                .get("/api/duck/action/quack")
                .queryParam("id", id)
                .queryParam("repetitionCount", String.valueOf(repetitionCount))
                .queryParam("soundCount", String.valueOf(soundCount)));
    }

    // заставить уточку плавать
    public void duckSwim(TestCaseRunner runner, String id) {
        runner.$(http().client(duckService)
                .send()
                .get("/api/duck/action/swim")
                .queryParam("id", id));
    }

    // получение свойств уточки
    public void duckProperties(TestCaseRunner runner, String id) {
        runner.$(http().client(duckService)
                .send()
                .get("/api/duck/action/properties")
                .queryParam("id", id));
    }

    // цикл для чётного id
    public AtomicInteger createDuckWithEvenId(TestCaseRunner runner) {
        AtomicInteger id;
        Duck duck = new Duck().color("yellow").height(0.07).material("wood").sound("quack").wingsState(WingState.ACTIVE);
        do {
            createDuck(runner, duck);
            id = extractId(runner);
        } while (id.get() % 2 != 0);
        return id;
    }

    // цикл для нечётного id
    public AtomicInteger createDuckWithOddId(TestCaseRunner runner) {
        AtomicInteger id;
        Duck duck = new Duck().color("yellow").height(0.04).material("rubber").sound("quack").wingsState(WingState.ACTIVE);
        do {
            createDuck(runner, duck);
            id = extractId(runner);
        } while (id.get() % 2 == 0);
        return id;
    }


    @Description("Валидация с передачей ответа String'ой")
    public void validateResponse(TestCaseRunner runner, HttpStatus httpStatus, String responseMessage) {
        runner.$(http().client(duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(responseMessage));
    }

    @Description("Валидация с передачей ответа из папки resources")
    public void validateResponse(TestCaseRunner runner, String expectedPayload) {
        runner.$(http().client(duckService)
                .receive()
                .response()
                .message().type(MessageType.JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new ClassPathResource(expectedPayload)));
    }

    @Description("Валидация с передачей ответа из папки Payloads")
    public void validateResponse(TestCaseRunner runner, Object expectedPayload) {
        runner.$(http().client(duckService)
                .receive()
                .response(HttpStatus.OK)
                .message().type(MessageType.JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new ObjectMappingPayloadBuilder(expectedPayload, new ObjectMapper())));
    }

    // проверка характеристик
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

    // извлечение id для всех методов
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

    public void validateCreateAndGetId(TestCaseRunner runner, String responseMessage) {
        runner.$(http().client(duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .extract(fromBody().expression("$.id", "duckId"))
                .body(new ClassPathResource(responseMessage)));
    }
}
