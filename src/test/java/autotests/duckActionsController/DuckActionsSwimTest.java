package autotests.duckActionsController;

import autotests.Payloads.Duck;
import autotests.Payloads.Message;
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
@Feature("Эндпоинт /api/duck/action/swim")
public class DuckActionsSwimTest extends DuckActionClient {
    @Test(description = "Проверка того, что уточка поплыла, существующий id")
    @CitrusTest
    public void swimDuckWithExistingId(@Optional @CitrusResource TestCaseRunner runner) {
        runner.variable("id", "citrus:randomNumber(10,true)");
        runner.$(doFinally().actions(action -> databaseDelete(runner, "${id}")));

        Duck duck = new Duck().color("yellow").height(0.04).material("wood").sound("quack").wingsState(WingState.ACTIVE);
        createDuckInBd(runner, "insert into DUCK (id, color, height, material, sound, wings_state)\n" +
                "values (${id}, '" + duck.color() + "', " + duck.height() + ", '" + duck.material() + "', '" + duck.sound() + "'" +
                ",'" + duck.wingsState() + "');");

        duckSwim(runner, "${id}");
        validateResponse(runner, "{" + " \"message\": \"I'm swimming\"" +
                "}", HttpStatus.OK);
    }

    @Test(description = "Проверка того, что уточка не поплыла, несуществующий id")
    @CitrusTest
    public void swimDuckWithNonExistingId(@Optional @CitrusResource TestCaseRunner runner) {
        runner.variable("id", "citrus:randomNumber(10,true)");
        runner.$(doFinally().actions(action -> databaseDelete(runner, "${id}")));

        Duck duck = new Duck().color("yellow").height(0.04).material("rubber").sound("quack").wingsState(WingState.ACTIVE);
        createDuckInBd(runner, "insert into DUCK (id, color, height, material, sound, wings_state)\n" +
                "values (${id}, '" + duck.color() + "', " + duck.height() + ", '" + duck.material() + "', '" + duck.sound() + "'" +
                ",'" + duck.wingsState() + "');");

        databaseUpdate(runner, "DELETE FROM DUCK WHERE id = ${id}");
        duckSwim(runner, "${id}");
        validateResponse(runner, "duckActionController/swimNonExistingId.json", HttpStatus.NO_CONTENT);
    }
}