package autotests.duckActionsController;

import clients.DuckActionClient;
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

public class DuckActionsSwimTest extends DuckActionClient {
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
}