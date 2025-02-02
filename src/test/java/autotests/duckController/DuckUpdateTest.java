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
@Feature("Эндпоинт /api/duck/update")
public class DuckUpdateTest extends DuckActionClient {
    @Test(description = "Проверка того, что уточке изменили цвет и её высоту")
    @CitrusTest
    public void successfulUpdateColorAndHeight(@Optional @CitrusResource TestCaseRunner runner) {
        runner.variable("id", "citrus:randomNumber(10,true)");
        runner.$(doFinally().actions(action -> databaseDelete(runner)));
        Duck duck = new Duck().color("yellow").height(15.0).material("rubber").sound("quack").wingsState(WingState.ACTIVE);
        createDuckInDb(runner, duck.color(), duck.height(), duck.material(), duck.sound(), duck.wingsState());

        duck.color("red");
        duck.height(7.0);

        duckUpdate(runner, duck.color(), duck.height(), duck.material(), duck.sound(), duck.wingsState());
        validateResponse(runner,  "{" +
                "  \"message\": \"Duck with id = ${id} is updated\"" + "}", HttpStatus.OK);
        validateDuckInDatabase(runner, "${id}", duck.color(), duck.height().toString(), duck.material(), duck.sound(), duck.wingsState().toString());
    }

    @Test(description = "Проверка того, что уточке изменили цвет и её звук")
    @CitrusTest
    public void successfulUpdateColorAndSound(@Optional @CitrusResource TestCaseRunner runner) {
        runner.variable("id", "citrus:randomNumber(10,true)");
        runner.$(doFinally().actions(action -> databaseDelete(runner)));
        Duck duck = new Duck().color("black").height(10.0).material("wood").sound("quack").wingsState(WingState.ACTIVE);
        createDuckInDb(runner, duck.color(), duck.height(), duck.material(), duck.sound(), duck.wingsState());

        duck.color("red");
        duck.sound("gav");

        duckUpdate(runner, duck.color(), duck.height(), duck.material(), duck.sound(), duck.wingsState());
        validateResponse(runner,  "{" +
                "  \"message\": \"Duck with id = ${id} is not updated\"" + "}", HttpStatus.BAD_REQUEST);
        validateDuckInDatabase(runner, "${id}", duck.color(), duck.height().toString(), duck.material(), duck.sound(), duck.wingsState().toString());
    }
}
