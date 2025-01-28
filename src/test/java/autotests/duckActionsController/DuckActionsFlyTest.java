package autotests.duckActionsController;

import autotests.Payloads.Duck;
import autotests.Payloads.WingState;
import clients.DuckActionClient;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.springframework.core.io.ClassPathResource;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

public class DuckActionsFlyTest extends DuckActionClient {
    @Test(description = "Проверка того, что уточка полетела, существующий id с активными крыльями")
    @CitrusTest
    public void successfulFly(@Optional @CitrusResource TestCaseRunner runner) {
        Duck duck = new Duck().color("yellow").height(0.04).material("rubber").sound("quack").wingsState(WingState.ACTIVE);
        createDuck(runner, duck);
        String id = extractId(runner).toString();
        duckFly(runner, id);
        validateResponse(runner, "duckActionController/flyWithActiveWings.json");
    }

    @Test(description = "Проверка того, что уточка не полетела, существующий id со связанными крыльями")
    @CitrusTest
    public void notSuccessfulFly(@Optional @CitrusResource TestCaseRunner runner) {
        Duck duck = new Duck().color("yellow").height(0.04).material("rubber").sound("quack").wingsState(WingState.FIXED);
        createDuck(runner, duck);
        String id = extractId(runner).toString();
        duckFly(runner, id);
        validateResponse(runner, "duckActionController/flyWithFixedWings.json");
    }

    @Test(description = "Проверка того, что крылья в неопределенном состоянии с существующим id")
    @CitrusTest
    public void undefinedWingsState(@Optional @CitrusResource TestCaseRunner runner) {
        Duck duck = new Duck().color("yellow").height(0.04).material("rubber").sound("quack").wingsState(WingState.UNDEFINED);
        createDuck(runner, duck);
        String id = extractId(runner).toString();
        duckFly(runner, id);
        validateResponse(runner, "duckActionController/flyWithUndefinedWings.json");
    }
}

