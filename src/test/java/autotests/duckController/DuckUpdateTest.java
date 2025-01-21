package autotests.duckController;

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

public class DuckUpdateTest extends TestNGCitrusSpringSupport {
    @Test(description = "Проверка того, что уточке изменили цвет и её высоту")
    @CitrusTest
    public void successfulUpdate1(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "yellow", 0.07, "rubber", "quack", "ACTIVE");
        runner.$(http().client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .extract(fromBody().expression("$.id", "duckId")));
        duckUpdate(runner, "red", 11.0, "${duckId}", "rubber", "quack", "ACTIVE");
        validateResponse(runner, HttpStatus.OK, "{\n" + " \"message\": \"Duck with id = ${duckId} is updated\"\n" + "}");
    }

    @Test(description = "Проверка того, что уточке изменили цвет и её звук")
    @CitrusTest
    public void successfulUpdate2(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "yellow", 5.0, "rubber", "quack", "ACTIVE");
        runner.$(http().client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .extract(fromBody().expression("$.id", "duckId")));
        duckUpdate(runner, "red", 5.0, "${duckId}", "rubber", "quack-quack", "ACTIVE");
        validateResponse(runner, HttpStatus.OK, "{\n" + " \"message\": \"Duck with id = ${duckId} is updated\"\n" + "}");
    }

    public void duckUpdate(TestCaseRunner runner, String color, double height, String id, String material, String sound, String wingsState) {
        runner.$(http().client("http://localhost:2222")
                .send()
                .put("/api/duck/update")
                .queryParam("color", color)
                .queryParam("height", String.valueOf(height))
                .queryParam("id", id)
                .queryParam("material", material)
                .queryParam("sound", sound)
                .queryParam("wingsState", wingsState));
    }

    public void validateResponse(TestCaseRunner runner, HttpStatus httpStatus, String responseMessage) {
        runner.$(http().client("http://localhost:2222")
                .receive()
                .response(httpStatus)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(responseMessage));
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
}
