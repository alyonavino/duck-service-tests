package tests;

import clients.DuckActionClient;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static com.consol.citrus.DefaultTestActionBuilder.action;
import static com.consol.citrus.dsl.MessageSupport.MessageBodySupport.fromBody;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckActionTest extends DuckActionClient {
    @Test(description = "Проверка того, что уточка полетела, существующий id с активными крыльями")
    @CitrusTest
    public void successfulFly(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "yellow", 0.07, "rubber", "quack", "ACTIVE");
        String id = extractId(runner).toString();
        duckFly(runner, id);
        validateResponse(runner, HttpStatus.OK, "{\n" + " \"message\": \"I am flying\"\n" + "}");
    }

    @Test(description = "Проверка того, что уточка не полетела, существующий id со связанными крыльями")
    @CitrusTest
    public void notSuccessfulFly(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "yellow", 15.0, "rubber", "quack", "FIXED");
        String id = extractId(runner).toString();
        duckFly(runner, id);
        validateResponse(runner, HttpStatus.OK, "{\n" + " \"message\": \"I can't fly\"\n" + "}");
    }

    @Test(description = "Проверка того, что крылья в неопределенном состоянии с существующим id")
    @CitrusTest
    public void undefinedWingsState(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "yellow", 11.0, "rubber", "quack", "UNDEFINED");
        String id = extractId(runner).toString();
        duckFly(runner, id);
        validateResponse(runner, HttpStatus.OK, "{\n" + " \"message\": \"Wings are not detected\"\n" + "}");
    }

    @Test(description = "Проверка того, что уточка издает корректный звук при корректном чётном id")
    @CitrusTest
    public void quackDuckWithEvenId(@Optional @CitrusResource TestCaseRunner runner) {
        AtomicInteger id;
        do {
            createDuck(runner, "yellow", 0.07, "wood", "quack", "ACTIVE");
            id = extractId(runner);
        } while (id.get() % 2 != 0);
        duckQuack(runner, id.toString(), 2, 3);
        validateResponse(runner, HttpStatus.OK, "{\n" + " \"sound\": \"quack-quack-quack, quack-quack-quack\"\n" + "}");
    }

    @Test(description = "Проверка того, что уточка издает корректный звук при корректном нечётном id")
    @CitrusTest
    public void quackDuckWithOddId(@Optional @CitrusResource TestCaseRunner runner) {
        AtomicInteger id;
        do {
            createDuck(runner, "yellow", 0.07, "wood", "quack", "ACTIVE");
            id = extractId(runner);
        } while (id.get() % 2 == 0);
        duckQuack(runner, id.toString(), 2, 3);
        validateResponse(runner, HttpStatus.OK, "{\n" + " \"sound\": \"quack-quack-quack, quack-quack-quack\"\n" + "}");
    }

    @Test(description = "Проверка того, что уточка поплыла, существующий id")
    @CitrusTest
    public void swimDuckWithExistingId(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner,"yellow", 0.07, "rubber", "quack", "FIXED");
        String duckId = extractId(runner).toString();
        duckSwim(runner, duckId);
        validateResponse(runner, HttpStatus.OK, "{\n" + " \"message\": \"I'm swimming\"\n" + "}");
    }

    @Test(description = "Проверка того, поплыла ли уточка, несуществующий id")
    @CitrusTest
    public void swimDuckWithNonExistingId(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner,"yellow", 0.07, "rubber", "quack", "FIXED");
        String duckId = Integer.toString(extractId(runner).incrementAndGet());
        duckSwim(runner, duckId);
        validateResponse(runner, HttpStatus.NO_CONTENT, String.format("{\n" + " \"message\": \"duck with id=%s is not found\"\n" + "}", duckId));
    }

    @Test(description = "Проверка того, что показываются характеристики при четном id и при material = wood")
    @CitrusTest
    public void getWoodDuckPropertiesEvenId(@Optional @CitrusResource TestCaseRunner runner) {
        AtomicInteger id;
        do {
            createDuck(runner, "yellow", 0.07, "wood", "quack", "ACTIVE");
            id = extractId(runner);
        } while (id.get() % 2 != 0);
        duckProperties(runner, id.toString());
        validateProperties(runner, "yellow", "0.07", "wood", "quack", "ACTIVE");
    }

    @Test(description = "Проверка того, что показываются характеристики при нечетном id и при material = rubber")
    @CitrusTest
    public void getRubberDuckPropertiesOddId(@Optional @CitrusResource TestCaseRunner runner) {
        AtomicInteger id;
        do {
            createDuck(runner, "yellow", 0.07, "rubber", "quack", "ACTIVE");
            id = extractId(runner);
        } while (id.get() % 2 == 0);
        duckProperties(runner, id.toString());
        validateProperties(runner, "yellow", "0.07", "rubber", "quack", "ACTIVE");
    }

    @Test(description = "Проверка того, что создалась уточка c material = rubber")
    @CitrusTest
    public void successfulCreateWithMaterialRubber(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "yellow", 0.04, "rubber", "quack", "ACTIVE");
        validateWithMaterial(runner, "yellow", "0.04", "rubber", "quack", "ACTIVE");
    }

    @Test(description = "Проверка того, что создалась уточка c material = wood")
    @CitrusTest
    public void successfulCreateWithMaterialWood(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "yellow", 0.08, "wood", "quack", "ACTIVE");
        validateWithMaterial(runner, "yellow", "0.08", "wood", "quack", "ACTIVE");
    }

    @Test(description = "Проверка того, что удалили уточку")
    @CitrusTest
    public void successfulDelete(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner,"yellow", 10.0, "rubber", "quack", "ACTIVE");
        String id = extractId(runner).toString();
        duckDelete(runner,id);
        validateResponse(runner,HttpStatus.OK, "{\n" + " \"message\": \"Duck is deleted\"\n" + "}");
    }

    @Test(description = "Проверка того, что уточке изменили цвет и её высоту")
    @CitrusTest
    public void successfulUpdateColorAndHeight(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "yellow", 0.07, "rubber", "quack", "ACTIVE");
        String id = extractId(runner).toString();
        duckUpdate(runner, "red", 11.0, id, "rubber", "quack", "ACTIVE");
        validateResponse(runner, HttpStatus.OK, String.format("{\n" + " \"message\": \"Duck with id = %s is updated\"\n" + "}", id));
    }

    @Test(description = "Проверка того, что уточке изменили цвет и её звук")
    @CitrusTest
    public void successfulUpdateColorAndSound(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "yellow", 5.0, "rubber", "quack", "ACTIVE");
        String id = extractId(runner).toString();
        duckUpdate(runner, "red", 5.0, id, "rubber", "quack-quack", "ACTIVE");
        validateResponse(runner, HttpStatus.OK, String.format("{\n" + " \"message\": \"Duck with id = %s is updated\"\n" + "}", id));
    }
}