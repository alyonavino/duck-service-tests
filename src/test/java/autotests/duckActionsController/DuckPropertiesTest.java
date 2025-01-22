package autotests.duckActionsController;

import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.JsonPathSupport;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static com.consol.citrus.DefaultTestActionBuilder.action;
import static com.consol.citrus.dsl.MessageSupport.MessageBodySupport.fromBody;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;

public class DuckPropertiesTest extends TestNGCitrusSpringSupport {
    @Test(description = "Проверка того, что показываются характеристики при четном id и при material = wood")
    @CitrusTest
    public void successfulSeePropertiesWithClearIdAndMaterialWood(@Optional @CitrusResource TestCaseRunner runner) {
        AtomicInteger id;
        do {
            createDuck(runner, "yellow", 0.07, "wood", "quack", "ACTIVE");
            id = extractId(runner);
        } while (id.get() % 2 != 0);
        duckProperties(runner, id.toString());
        validateProperties(runner, "yellow", "0.07", "wood", "quack", "ACTIVE");
    }

    @Test(description = "Проверка того, что показываются характеристики при нечетном id и при material = rubber")
    @CitrusTest
    public void successfulSeePropertiesWithNonClearIdAndMaterialRubber(@Optional @CitrusResource TestCaseRunner runner) {
        AtomicInteger id;
        do {
            createDuck(runner, "yellow", 0.07, "rubber", "quack", "ACTIVE");
            id = extractId(runner);
        } while (id.get() % 2 == 0);
        duckProperties(runner, id.toString());
        validateProperties(runner, "yellow", "0.07", "rubber", "quack", "ACTIVE");
    }

    private void validateProperties(TestCaseRunner runner, String color, String height, String material, String sound, String wingsState) {
        runner.$(http().client("http://localhost:2222")
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

    public void createDuck(TestCaseRunner runner, String color, double height, String material, String sound, String wingsState) {
        runner.$(http().client("http://localhost:2222")
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

    public void duckProperties(TestCaseRunner runner, String id) {
        runner.$(http().client("http://localhost:2222")
                .send()
                .get("/api/duck/action/properties")
                .queryParam("id", id));
    }

    private AtomicInteger extractId(TestCaseRunner runner) {
        AtomicInteger id = new AtomicInteger();
        runner.$(http().client("http://localhost:2222")
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