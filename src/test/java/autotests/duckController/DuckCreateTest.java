package autotests.duckController;

import autotests.Payloads.Duck;
import autotests.Payloads.WingState;
import clients.DuckActionClient;
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

public class DuckCreateTest extends DuckActionClient {
    @Test(description = "Проверка того, что создалась уточка c material = rubber")
    @CitrusTest
    public void successfulCreateWithMaterialRubber(@Optional @CitrusResource TestCaseRunner runner) {
        Duck duck = new Duck().color("yellow").height(0.04).material("rubber").sound("quack").wingsState(WingState.ACTIVE);
        createDuck(runner, duck);
        validateCreateAndGetId(runner, "duckController/duckRubber.json");
    }

    @Test(description = "Проверка того, что создалась уточка c material = wood")
    @CitrusTest
    public void successfulCreateWithMaterialWood(@Optional @CitrusResource TestCaseRunner runner) {
        Duck duck = new Duck().color("yellow").height(0.04).material("wood").sound("quack").wingsState(WingState.ACTIVE);
        createDuck(runner, duck);
        validateCreateAndGetId(runner, "duckController/duckWood.json");
    }
}

