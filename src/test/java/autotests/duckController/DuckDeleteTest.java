package autotests.duckController;

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

@Epic("Тесты на duck-controller")
@Feature("Эндпоинт /api/duck/delete")
public class DuckDeleteTest extends DuckActionClient {
    @Test(description = "Проверка того, что удалили уточку")
    @CitrusTest
    public void successfulDelete(@Optional @CitrusResource TestCaseRunner runner) {
        runner.variable("id", "citrus:randomNumber(10,true)");
        runner.$(doFinally().actions(action -> databaseDelete(runner)));
        Duck duck = new Duck().color("yellow").height(10.0).material("rubber").sound("quack").wingsState(WingState.ACTIVE);
        createDuckInDb(runner, duck.color(), duck.height(), duck.material(), duck.sound(), duck.wingsState());
        duckDelete(runner);
        validateResponse(runner, "{" + " \"message\": \"Duck is deleted\"" +
                "}", HttpStatus.OK);

    }
}

