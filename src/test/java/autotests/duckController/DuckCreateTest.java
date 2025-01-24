package autotests.duckController;

import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.dsl.JsonPathSupport.jsonPath;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckCreateTest extends TestNGCitrusSpringSupport {
    @Test(description = "Проверка того, что создалась уточка c material = rubber")
    @CitrusTest
    public void successfulCreateWithMaterialRubber(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "yellow", 0.04, "rubber", "quack", "ACTIVE");
        validateWithMaterial(runner, "yellow", "0.04", "rubber", "quack", "ACTIVE");
    }

    @Test(description = "Проверка того, что создалась уточка c material = wood")
    @CitrusTest
    public void successfulCreateWithMaterialWood(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "yellow", 0.08, "wood", "quack", "ACTIVE");
        validateWithMaterial(runner, "yellow", "0.08", "wood", "quack", "ACTIVE");
    }

    public void validateWithMaterial(TestCaseRunner runner, String color, String height, String material, String sound, String wingsState) {
        runner.$(http().client("http://localhost:2222")
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

    public void createDuck(TestCaseRunner runner, String color, double height, String material, String sound, String wingsState){
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
}

