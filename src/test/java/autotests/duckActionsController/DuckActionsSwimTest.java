package autotests.duckActionsController;

import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.dsl.MessageSupport.MessageBodySupport.fromBody;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckActionsSwimTest extends TestNGCitrusSpringSupport {
    @Test(description = "Проверка того, что уточка поплыла, существующий id")
    @CitrusTest
    public void successfulSwim(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner,"yellow", 0.07, "rubber", "quack", "FIXED");
        runner.$(http().client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .extract(fromBody().expression("$.id", "duckId")));
        duckSwim(runner, "${duckId}");
        validateResponse(runner, HttpStatus.OK, "{\n" + " \"message\": \"I'm swimming\"\n" + "}");
    }

    @Test(description = "Проверка того, поплыла ли уточка, несущестующий id")
    @CitrusTest
    public void nonExistenceId(@Optional @CitrusResource TestCaseRunner runner) {
        variable("duckId", "1000");
        createDuck(runner,"yellow", 0.07, "rubber", "quack", "FIXED");
        runner.$(http().client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK));
        duckSwim(runner, "${duckId}");
        validateResponse(runner, HttpStatus.NO_CONTENT, "{\n" + " \"message\": \"duck with id=${duckId} is not found\"\n" + "}");
    }

    public void duckSwim(TestCaseRunner runner, String id) {
        runner.$(http().client("http://localhost:2222")
                .send()
                .get("/api/duck/action/swim")
                .queryParam("id", id));
    }

    public void validateResponse(TestCaseRunner runner, HttpStatus httpStatus, String responseMessage) {
        runner.$(http().client("http://localhost:2222")
                .receive()
                .response(httpStatus)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(responseMessage));
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