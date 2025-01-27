package autotests.duckActionsController;

import clients.DuckActionClient;
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

public class DuckPropertiesTest extends DuckActionClient {
    @Test(description = "Проверка того, что показываются характеристики при четном id и при material = wood")
    @CitrusTest
    public void getWoodDuckPropertiesEvenId(@Optional @CitrusResource TestCaseRunner runner) {
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
    public void getRubberDuckPropertiesOddId(@Optional @CitrusResource TestCaseRunner runner) {
        AtomicInteger id;
        do {
            createDuck(runner, "yellow", 0.07, "rubber", "quack", "ACTIVE");
            id = extractId(runner);
        } while (id.get() % 2 == 0);
        duckProperties(runner, id.toString());
        validateProperties(runner, "yellow", "0.07", "rubber", "quack", "ACTIVE");
    }
}