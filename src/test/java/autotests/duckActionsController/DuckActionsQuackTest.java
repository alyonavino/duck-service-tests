package autotests.duckActionsController;

import autotests.Payloads.Sound;
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

public class DuckActionsQuackTest extends DuckActionClient {
    @Test(description = "Проверка того, что уточка издает корректный звук при корректном чётном id")
    @CitrusTest
    public void quackDuckWithEvenId(@Optional @CitrusResource TestCaseRunner runner) {
        AtomicInteger id = createDuckWithEvenId(runner);
        duckQuack(runner, id.toString(), 2, 3);
        Sound sound = new Sound().sound("quack-quack-quack, quack-quack-quack");
        validateResponse(runner, sound);
    }

    @Test(description = "Проверка того, что уточка издает корректный звук при корректном нечётном id")
    @CitrusTest
    public void quackDuckWithOddId(@Optional @CitrusResource TestCaseRunner runner) {
        AtomicInteger id = createDuckWithOddId(runner);
        duckQuack(runner, id.toString(), 2, 3);
        Sound sound = new Sound().sound("quack-quack-quack, quack-quack-quack");
        validateResponse(runner, sound);
    }
}