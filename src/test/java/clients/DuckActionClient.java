package clients;

import autotests.EndpointConfig;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.dsl.JsonPathSupport;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void createDuck(TestCaseRunner runner, String color, double height, String material, String sound, String wingsState) {
        runner.$(http().client(duckService)
                .send()
                .post("/api/duck/create")
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\n" +
                        "\"color\": \"" + color + "\",\n" +
                        "\"height\": " + height + ",\n" +
                        "\"material\": \"" + material + "\",\n" +
                        "\"sound\": \"" + sound + "\",\n" +
                        "\"wingsState\": \"" + wingsState + "\"\n" + "}"));
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
        do {
            createDuck(runner, "yellow", 0.07, "wood", "quack", "ACTIVE");
            id = extractId(runner);
        } while (id.get() % 2 != 0);
        return id;
    }

// цикл для нечётного id
    public AtomicInteger createDuckWithOddId(TestCaseRunner runner) {
        AtomicInteger id;
        do {
            createDuck(runner, "yellow", 0.07, "wood", "quack", "ACTIVE");
            id = extractId(runner);
        } while (id.get() % 2 == 0);
        return id;
    }

// валидирование ответа для всех методов
public void validateResponse(TestCaseRunner runner, HttpStatus httpStatus, String responseMessage) {
    runner.$(http().client(duckService)
            .receive()
            .response(httpStatus)
            .message()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(responseMessage));
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

// проверка на правильное создание уточки со всеми свойствами
public void validateAllProperties(TestCaseRunner runner, String color, String height, String material, String sound, String wingsState) {
    runner.$(http().client(duckService)
            .receive()
            .response(HttpStatus.OK)
            .message()
            .type(MessageType.JSON)
            .validate(jsonPath().expression("$.color", color))
            .validate(jsonPath().expression("$.height", height))
            .validate(jsonPath().expression("$.material", material))
            .validate(jsonPath().expression("$.sound", sound))
            .validate(jsonPath().expression("$.wingsState", wingsState)));
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


}
