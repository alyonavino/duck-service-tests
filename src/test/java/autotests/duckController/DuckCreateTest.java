package autotests.duckController;

import autotests.Payloads.Duck;
import autotests.Payloads.WingState;
import clients.DuckActionClient;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;

import com.consol.citrus.testng.CitrusParameters;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.springframework.http.HttpStatus;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;


import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

@Epic("Тесты на duck-controller")
@Feature("Эндпоинт /api/duck/create")
public class DuckCreateTest extends DuckActionClient {
        Duck duck1 = new Duck()
                .color("yellow")
                .height(10.0)
                .material("rubber")
                .sound("quack")
                .wingsState(WingState.FIXED);
        Duck duck2 = new Duck()
                .color("black")
                .height(7.0)
                .material("wood")
                .sound("quack")
                .wingsState(WingState.FIXED);
        Duck duck3 = new Duck()
                .color("red")
                .height(14.0)
                .material("rubber")
                .sound("quack")
                .wingsState(WingState.ACTIVE);
        Duck duck4 = new Duck()
                .color("pink")
                .height(5.7)
                .material("wood")
                .sound("quack")
                .wingsState(WingState.ACTIVE);
        Duck duck5 = new Duck()
                .color("green")
                .height(7.7)
                .material("rubber")
                .sound("quack")
                .wingsState(WingState.ACTIVE);
    @Test(description = "Создание 5 разных уточек")
    @CitrusTest
    @CitrusParameters({"payload","response","runner"})
    public void createDucks(Object payload, Object response, @Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, payload);
        validateResponseFromPayloads(runner, response, HttpStatus.OK);
        databaseDelete(runner);
    }
    @DataProvider(name="duckList")
    public Object[][] DuckProvider() {
        return new Object[][] {
                {duck1, "duckController/duckCreate1.json" ,null},
                {duck2, "duckController/duckCreate2.json", null},
                {duck3, "duckController/duckCreate3.json", null},
                {duck4, "duckController/duckCreate4.json", null},
                {duck5, "duckController/duckCreate5.json", null}
            };
    }

    @Test(description = "Проверка того, что создалась уточка c material = rubber")
    @CitrusTest
    public void successfulCreateWithMaterialRubber(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(action -> databaseDelete(runner)));
        Duck duck = new Duck().color("yellow").height(0.04).material("rubber").sound("quack").wingsState(WingState.ACTIVE);
        createDuck(runner, duck);
        validateResponseFromResources(runner, "duckController/duckRubber.json", HttpStatus.OK);
        validateDuckInDatabase(runner, "${id}", duck.color(), duck.height().toString(), duck.material(), duck.sound(), duck.wingsState().toString());
    }

    @Test(description = "Проверка того, что создалась уточка c material = wood")
    @CitrusTest
    public void successfulCreateWithMaterialWood(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(action -> databaseDelete(runner)));
        Duck duck = new Duck().color("yellow").height(0.08).material("wood").sound("quack").wingsState(WingState.ACTIVE);
        createDuck(runner, duck);
        validateResponseFromResources(runner, "duckController/duckWood.json", HttpStatus.OK);
        validateDuckInDatabase(runner, "${id}", duck.color(), duck.height().toString(), duck.material(), duck.sound(), duck.wingsState().toString());
    }
}

