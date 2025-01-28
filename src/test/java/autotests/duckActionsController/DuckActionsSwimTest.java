package autotests.duckActionsController;

import autotests.Payloads.Duck;
import autotests.Payloads.Message;
import autotests.Payloads.WingState;
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
        Duck duck = new Duck().color("yellow").height(0.04).material("wood").sound("quack").wingsState(WingState.ACTIVE);
        createDuck(runner, duck);
        String duckId = extractId(runner).toString();
        duckSwim(runner, duckId);
        Message message = new Message().message("I'm swimming");
        validateResponse(runner, message);
    }

    @Test(description = "Проверка того, поплыла ли уточка, несуществующий id")
    @CitrusTest
    public void swimDuckWithNonExistingId(@Optional @CitrusResource TestCaseRunner runner) {
        Duck duck = new Duck().color("yellow").height(0.04).material("rubber").sound("quack").wingsState(WingState.ACTIVE);
        createDuck(runner, duck);
        String duckId = Integer.toString(extractId(runner).incrementAndGet());
        duckSwim(runner, duckId);
        validateResponse(runner, "duckActionController/swimNonExistingId.json");
    }
}