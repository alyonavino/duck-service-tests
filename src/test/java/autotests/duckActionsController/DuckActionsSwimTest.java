package autotests.duckActionsController;

import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static com.consol.citrus.DefaultTestActionBuilder.action;
import static com.consol.citrus.dsl.MessageSupport.MessageBodySupport.fromBody;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckActionsSwimTest extends TestNGCitrusSpringSupport {
    @Test(description = "Проверка того, что уточка поплыла, существующий id")
    @CitrusTest
    public void swimDuckWithExistingId(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner,"yellow", 0.07, "rubber", "quack", "FIXED");
        String duckId = extractId(runner).toString();
        duckSwim(runner, duckId);
        validateResponse(runner, HttpStatus.OK, "{\n" + " \"message\": \"I'm swimming\"\n" + "}");
    }

    @Test(description = "Проверка того, поплыла ли уточка, несуществующий id")
    @CitrusTest
    public void swimDuckWithNonExistingId(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner,"yellow", 0.07, "rubber", "quack", "FIXED");
        String duckId = Integer.toString(extractId(runner).incrementAndGet());
        duckSwim(runner, duckId);
        validateResponse(runner, HttpStatus.NO_CONTENT, String.format("{\n" + " \"message\": \"duck with id=%s is not found\"\n" + "}", duckId));
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

    private AtomicInteger extractId(TestCaseRunner runner) {
        AtomicInteger id = new AtomicInteger();
        runner.$(http().client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .extract(fromBody().expression("$.id", "duckId")));
        runner.$(action(context -> {
            id.set( context.getVariable("${duckId}", int.class));
        }));
        return id;
    }
}