package autotests.duckActionsController;

import autotests.Payloads.Duck;
import autotests.Payloads.WingState;
import clients.DuckActionClient;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;
@Epic("Тесты на duck-action-controller")
@Feature("Эндпоинт /api/duck/action/fly")
public class DuckActionsFlyTest extends DuckActionClient {
    @Test(description = "Проверка того, что уточка полетела, существующий id с активными крыльями")
    @CitrusTest
    public void successfulFly(@Optional @CitrusResource TestCaseRunner runner) {
        runner.variable("id", "1234567");
        runner.$(doFinally().actions(action -> createDuckInBd(runner, "DELETE FROM DUCK WHERE ID = ${id}")));

        Duck duck = new Duck().color("yellow").height(15.0).material("rubber").sound("quack").wingsState(WingState.ACTIVE);

        createDuckInBd(runner, "insert into DUCK (id, color, height, material, sound, wings_state)\n" +
                "values (${id}, '" + duck.color() + "', " + duck.height() + ", '" + duck.material() + "', '" + duck.sound() + "'" +
                ",'" + duck.wingsState() + "');");

        duckFly(runner, "${id}");
        validateResponse(runner, "duckActionController/flyWithActiveWings.json");
    }

    @Test(description = "Проверка того, что уточка не полетела, существующий id со связанными крыльями")
    @CitrusTest
    public void notSuccessfulFly(@Optional @CitrusResource TestCaseRunner runner) {
        runner.variable("id", "1234567");
        runner.$(doFinally().actions(action -> createDuckInBd(runner, "DELETE FROM DUCK WHERE ID = ${id}")));

        Duck duck = new Duck().color("yellow").height(15.0).material("rubber").sound("quack").wingsState(WingState.FIXED);

        createDuckInBd(runner, "insert into DUCK (id, color, height, material, sound, wings_state)\n" +
                "values (${id}, '" + duck.color() + "', " + duck.height() + ", '" + duck.material() + "', '" + duck.sound() + "'" +
                ",'" + duck.wingsState() + "');");

        duckFly(runner, "${id}");
        validateResponse(runner, "duckActionController/flyWithFixedWings.json");
    }

    @Test(description = "Проверка того, что крылья в неопределенном состоянии с существующим id")
    @CitrusTest
    public void undefinedWingsState(@Optional @CitrusResource TestCaseRunner runner) {
        runner.variable("id", "1234567");
        runner.$(doFinally().actions(action -> createDuckInBd(runner, "DELETE FROM DUCK WHERE ID = ${id}")));

        Duck duck = new Duck().color("yellow").height(15.0).material("rubber").sound("quack").wingsState(WingState.UNDEFINED);

        createDuckInBd(runner, "insert into DUCK (id, color, height, material, sound, wings_state)\n" +
                "values (${id}, '" + duck.color() + "', " + duck.height() + ", '" + duck.material() + "', '" + duck.sound() + "'" +
                ",'" + duck.wingsState() + "');");

        duckFly(runner, "${id}");
        validateResponse(runner, "duckActionController/flyWithUndefinedWings.json");
    }
}

