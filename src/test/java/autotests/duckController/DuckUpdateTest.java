package autotests.duckController;

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

public class DuckUpdateTest extends DuckActionClient {
    @Test(description = "Проверка того, что уточке изменили цвет и её высоту")
    @CitrusTest
    public void successfulUpdateColorAndHeight(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "yellow", 0.07, "rubber", "quack", "ACTIVE");
        String id = extractId(runner).toString();
        duckUpdate(runner, "red", 11.0, id, "rubber", "quack", "ACTIVE");
        validateResponse(runner, HttpStatus.OK, String.format("{\n" + " \"message\": \"Duck with id = %s is updated\"\n" + "}", id));
    }

    @Test(description = "Проверка того, что уточке изменили цвет и её звук")
    @CitrusTest
    public void successfulUpdateColorAndSound(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "yellow", 5.0, "rubber", "quack", "ACTIVE");
        String id = extractId(runner).toString();
        duckUpdate(runner, "red", 5.0, id, "rubber", "quack-quack", "ACTIVE");
        validateResponse(runner, HttpStatus.OK, String.format("{\n" + " \"message\": \"Duck with id = %s is updated\"\n" + "}", id));
    }
}
