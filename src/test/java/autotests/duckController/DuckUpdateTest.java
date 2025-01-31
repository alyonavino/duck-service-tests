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
        runner.variable("id", "1234567");
        runner.$(doFinally().actions(action -> createDuckInBd(runner, "DELETE FROM DUCK WHERE ID = ${id}")));

        Duck duck = new Duck().color("yellow").height(15.0).material("rubber").sound("quack").wingsState(WingState.ACTIVE);

        createDuckInBd(runner, "insert into DUCK (id, color, height, material, sound, wings_state)\n" +
                "values (${id}, '" + duck.color() + "', " + duck.height() + ", '" + duck.material() + "', '" + duck.sound() + "'" +
                ",'" + duck.wingsState() + "');");

        duck.color("red");
        duck.height(7.0);

        duckUpdate(runner, duck.color(), duck.height(), duck.material(), duck.sound(), duck.wingsState());
        validateResponse(runner, HttpStatus.OK, "{" +
                "  \"message\": \"Duck with id = ${id} is updated\"" + "}");
        validateDuckInDatabase(runner, "${id}", duck.color(), duck.height().toString(), duck.material(), duck.sound(), duck.wingsState().toString());
    }

    @Test(description = "Проверка того, что уточке изменили цвет и её звук")
    @CitrusTest
    public void successfulUpdateColorAndSound(@Optional @CitrusResource TestCaseRunner runner) {
        runner.variable("id", "1234567");
        runner.$(doFinally().actions(action -> createDuckInBd(runner, "DELETE FROM DUCK WHERE ID = ${id}")));

        Duck duck = new Duck().color("black").height(10.0).material("wood").sound("quack").wingsState(WingState.ACTIVE);

        createDuckInBd(runner, "insert into DUCK (id, color, height, material, sound, wings_state)\n" +
                "values (${id}, '" + duck.color() + "', " + duck.height() + ", '" + duck.material() + "', '" + duck.sound() + "'" +
                ",'" + duck.wingsState() + "');");

        duck.color("red");
        duck.sound("gav");

        duckUpdate(runner, duck.color(), duck.height(), duck.material(), duck.sound(), duck.wingsState());
        validateResponse(runner, HttpStatus.BAD_REQUEST, "{" +
                "  \"message\": \"Duck with id = ${id} is not updated\"" + "}");
        validateDuckInDatabase(runner, "${id}", duck.color(), duck.height().toString(), duck.material(), duck.sound(), duck.wingsState().toString());
    }
}
