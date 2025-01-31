package autotests.duckActionsController;

import autotests.Payloads.Duck;
import autotests.Payloads.Sound;
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

import java.util.concurrent.atomic.AtomicInteger;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

@Epic("Тесты на duck-action-controller")
@Feature("Эндпоинт /api/duck/action/quack")
public class DuckActionsQuackTest extends DuckActionClient {
    @Test(description = "Проверка того, что уточка издает корректный звук при корректном чётном id")
    @CitrusTest
    public void quackDuckWithEvenId(@Optional @CitrusResource TestCaseRunner runner) {
       AtomicInteger id = getRandomId(runner, true);
       runner.$(doFinally().actions(action -> databaseDelete(runner, "${id}")));

       Duck duck = new Duck().color("yellow").height(0.04).material("wood").sound("quack").wingsState(WingState.ACTIVE);
       createDuckInBd(runner, "insert into DUCK (id, color, height, material, sound, wings_state)\n" +
                "values (${id}, '" + duck.color() + "', " + duck.height() + ", '" + duck.material() + "', '" + duck.sound() + "'" +
                ",'" + duck.wingsState() + "');");

       duckQuack(runner, id.toString(), 2, 3);
       validateResponse(runner, "{" + " \"message\": \"quack-quack-quack, quack-quack-quack\"" +
                "}", HttpStatus.OK);
    }

    @Test(description = "Проверка того, что уточка издает корректный звук при корректном нечётном id")
    @CitrusTest
    public void quackDuckWithOddId(@Optional @CitrusResource TestCaseRunner runner) {
        AtomicInteger id = getRandomId(runner, false);
        runner.$(doFinally().actions(action -> databaseDelete(runner, "${id}")));

        Duck duck = new Duck().color("yellow").height(0.04).material("wood").sound("quack").wingsState(WingState.ACTIVE);
        createDuckInBd(runner, "insert into DUCK (id, color, height, material, sound, wings_state)\n" +
                "values (${id}, '" + duck.color() + "', " + duck.height() + ", '" + duck.material() + "', '" + duck.sound() + "'" +
                ",'" + duck.wingsState() + "');");

        duckQuack(runner, id.toString(), 2, 3);
        Sound sound = new Sound().sound("quack-quack-quack, quack-quack-quack");
        validateResponse(runner, "{" + " \"message\": \"quack-quack-quack, quack-quack-quack\"" +
                "}", HttpStatus.OK);
    }
}