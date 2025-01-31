package autotests.duckController;

import autotests.Payloads.Duck;
import autotests.Payloads.WingState;
import clients.DuckActionClient;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.actions.ExecuteSQLAction.Builder.sql;

@Epic("Тесты на duck-controller")
@Feature("Эндпоинт /api/duck/create")
public class DuckCreateTest extends DuckActionClient {
    @Test(description = "Проверка того, что создалась уточка c material = rubber")
    @CitrusTest
    public void successfulCreateWithMaterialRubber(@Optional @CitrusResource TestCaseRunner runner) {
        Duck duck = new Duck().color("yellow").height(0.04).material("rubber").sound("quack").wingsState(WingState.ACTIVE);
        createDuck(runner, duck);
        String id = extractId(runner).toString();
        validateDuckInDatabase(runner, id, "yellow", "0.04", "rubber", "quack", "ACTIVE");
    }

    @Epic("Тесты на duck-controller")
    @Feature("Эндпоинт /api/duck/create")
    @Test(description = "Проверка того, что создалась уточка c material = wood")
    @CitrusTest
    public void successfulCreateWithMaterialWood(@Optional @CitrusResource TestCaseRunner runner) {
        Duck duck = new Duck().color("yellow").height(0.04).material("wood").sound("quack").wingsState(WingState.ACTIVE);
        createDuck(runner, duck);
        String id = extractId(runner).toString();
        validateDuckInDatabase(runner, id, "yellow", "0.04", "wood", "quack", "ACTIVE");
    }
}

