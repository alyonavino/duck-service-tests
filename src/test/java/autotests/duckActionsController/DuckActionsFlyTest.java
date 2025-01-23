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

public class DuckActionsFlyTest extends TestNGCitrusSpringSupport {
    @Test(description = "Проверка того, что уточка полетела, существующий id с активными крыльями")
    @CitrusTest
    public void successfulFly(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner,"yellow", 0.07, "rubber", "quack", "ACTIVE");
        String id = extractId(runner).toString();
        duckFly(runner, id);
        validateResponse(runner, HttpStatus.OK, "{\n" + " \"message\": \"I am flying\"\n" + "}");
    }

    @Test(description = "Проверка того, что уточка не полетела, существующий id со связанными крыльями")
    @CitrusTest
    public void notSuccessfulFly(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner,"yellow", 15.0, "rubber", "quack", "FIXED");
        String id = extractId(runner).toString();
        duckFly(runner, id);
        validateResponse(runner, HttpStatus.OK, "{\n" + " \"message\": \"I can't fly\"\n" + "}");
    }

    @Test(description = "Проверка того, что крылья в неопределенном состоянии с существующим id")
    @CitrusTest
    public void undefinedWingsState(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner,"yellow", 11.0, "rubber", "quack", "UNDEFINED");
        String id = extractId(runner).toString();
        duckFly(runner, id);
        validateResponse(runner, HttpStatus.OK, "{\n" + " \"message\": \"Wings are not detected\"\n" + "}");
    }

    public void duckFly(TestCaseRunner runner, String id) {
        runner.$(http().client("http://localhost:2222")
                .send()
                .get("/api/duck/action/fly")
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
            id.set(context.getVariable("${duckId}", int.class));
        }));
        return id;
    }
}

