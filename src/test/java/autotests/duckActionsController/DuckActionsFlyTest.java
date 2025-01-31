package autotests.duckActionsController;

import autotests.Payloads.Duck;
import autotests.Payloads.WingState;
import clients.DuckActionClient;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;
@Epic("Тесты на duck-action-controller")
@Feature("Эндпоинт /api/duck/action/fly")
public class DuckActionsFlyTest extends DuckActionClient {
    @Test(description = "Проверка того, что уточка полетела, существующий id с активными крыльями")
    @CitrusTest
    public void successfulFly(@Optional @CitrusResource TestCaseRunner runner) {
        runner.variable("id", "citrus:randomNumber(10,true)");
        runner.$(doFinally().actions(action -> databaseDelete(runner, "${id}")));

        Duck duck = new Duck().color("yellow").height(15.0).material("rubber").sound("quack").wingsState(WingState.ACTIVE);

        createDuckInBd(runner, "insert into DUCK (id, color, height, material, sound, wings_state)\n" +
                "values (${id}, '" + duck.color() + "', " + duck.height() + ", '" + duck.material() + "', '" + duck.sound() + "'" +
                ",'" + duck.wingsState() + "');");

        duckFly(runner, "${id}");
        validateResponseFromResources(runner, "duckActionController/flyWithActiveWings.json", HttpStatus.OK);
    }

    @Test(description = "Проверка того, что уточка не полетела, существующий id со связанными крыльями")
    @CitrusTest
    public void notSuccessfulFly(@Optional @CitrusResource TestCaseRunner runner) {
        runner.variable("id", "citrus:randomNumber(10,true)");
        runner.$(doFinally().actions(action -> databaseDelete(runner, "${id}")));

        Duck duck = new Duck().color("yellow").height(15.0).material("rubber").sound("quack").wingsState(WingState.FIXED);

        createDuckInBd(runner, "insert into DUCK (id, color, height, material, sound, wings_state)\n" +
                "values (${id}, '" + duck.color() + "', " + duck.height() + ", '" + duck.material() + "', '" + duck.sound() + "'" +
                ",'" + duck.wingsState() + "');");

        duckFly(runner, "${id}");
        validateResponseFromResources(runner, "duckActionController/flyWithFixedWings.json", HttpStatus.OK);
    }

    @Test(description = "Проверка того, что крылья в неопределенном состоянии с существующим id")
    @CitrusTest
    public void undefinedWingsState(@Optional @CitrusResource TestCaseRunner runner) {
        runner.variable("id", "citrus:randomNumber(10,true)");
        runner.$(doFinally().actions(action -> databaseDelete(runner, "${id}")));

        Duck duck = new Duck().color("yellow").height(15.0).material("rubber").sound("quack").wingsState(WingState.UNDEFINED);

        createDuckInBd(runner, "insert into DUCK (id, color, height, material, sound, wings_state)\n" +
                "values (${id}, '" + duck.color() + "', " + duck.height() + ", '" + duck.material() + "', '" + duck.sound() + "'" +
                ",'" + duck.wingsState() + "');");

        duckFly(runner, "${id}");
        validateResponseFromResources(runner, "duckActionController/flyWithUndefinedWings.json", HttpStatus.OK);
    }
}

