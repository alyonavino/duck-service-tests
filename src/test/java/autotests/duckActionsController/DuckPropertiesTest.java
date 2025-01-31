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

import java.util.concurrent.atomic.AtomicInteger;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

@Epic("Тесты на duck-action-controller")
@Feature("Эндпоинт /api/duck/action/properties")
public class DuckPropertiesTest extends DuckActionClient {
    @Test(description = "Проверка того, что показываются характеристики при четном id и при material = wood")
    @CitrusTest
    public void getWoodDuckPropertiesEvenId(@Optional @CitrusResource TestCaseRunner runner) {
        AtomicInteger id = getRandomId(runner, true);
        runner.$(doFinally().actions(action -> createDuckInBd(runner, "DELETE FROM DUCK WHERE ID = ${id}")));

        Duck duck = new Duck().color("yellow").height(0.04).material("wood").sound("quack").wingsState(WingState.ACTIVE);
        createDuckInBd(runner, "insert into DUCK (id, color, height, material, sound, wings_state)\n" +
                "values (${id}, '" + duck.color() + "', " + duck.height() + ", '" + duck.material() + "', '" + duck.sound() + "'" +
                ",'" + duck.wingsState() + "');");
        duckProperties(runner, id.toString());
        validateResponse(runner, "duckActionController/propertiesWoodDuck.json");
    }

    @Test(description = "Проверка того, что показываются характеристики при нечетном id и при material = rubber")
    @CitrusTest
    public void getRubberDuckPropertiesOddId(@Optional @CitrusResource TestCaseRunner runner) {
        AtomicInteger id = getRandomId(runner, false);
        runner.$(doFinally().actions(action -> createDuckInBd(runner, "DELETE FROM DUCK WHERE ID = ${id}")));

        Duck duck = new Duck().color("yellow").height(0.07).material("rubber").sound("quack").wingsState(WingState.ACTIVE);
        createDuckInBd(runner, "insert into DUCK (id, color, height, material, sound, wings_state)\n" +
                "values (${id}, '" + duck.color() + "', " + duck.height() + ", '" + duck.material() + "', '" + duck.sound() + "'" +
                ",'" + duck.wingsState() + "');");
        duckProperties(runner, id.toString());
        validateResponse(runner, "duckActionController/propertiesRubberDuck.json");
    }
}