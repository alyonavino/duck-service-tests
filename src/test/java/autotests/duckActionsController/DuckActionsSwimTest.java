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
@Feature("Эндпоинт /api/duck/action/swim")
public class DuckActionsSwimTest extends DuckActionClient {
    @Test(description = "Проверка того, что уточка поплыла, существующий id")
    @CitrusTest
    public void swimDuckWithExistingId(@Optional @CitrusResource TestCaseRunner runner) {
        runner.variable("id", "citrus:randomNumber(10,true)");
        runner.$(doFinally().actions(action -> databaseDelete(runner)));

        Duck duck = new Duck().color("yellow").height(0.04).material("wood").sound("quack").wingsState(WingState.ACTIVE);
        createDuckInDb(runner, duck.color(), duck.height(), duck.material(), duck.sound(), duck.wingsState());

        duckSwim(runner, "${id}");
        validateResponse(runner, "{" + " \"message\": \"I'm swimming\"" +
                "}", HttpStatus.OK);
    }

    @Test(description = "Проверка того, что уточка не поплыла, несуществующий id")
    @CitrusTest
    public void swimDuckWithNonExistingId(@Optional @CitrusResource TestCaseRunner runner) {
        runner.variable("id", "citrus:randomNumber(10,true)");
        runner.$(doFinally().actions(action -> databaseDelete(runner)));
        Duck duck = new Duck().color("yellow").height(0.04).material("rubber").sound("quack").wingsState(WingState.ACTIVE);
        createDuckInDb(runner, duck.color(), duck.height(), duck.material(), duck.sound(), duck.wingsState());
        databaseUpdate(runner, "DELETE FROM DUCK WHERE id = ${id}");
        duckSwim(runner, "${id}");
        validateResponseFromResources(runner, "duckActionController/swimNonExistingId.json", HttpStatus.NOT_FOUND);
    }
}