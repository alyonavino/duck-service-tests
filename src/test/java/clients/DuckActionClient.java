package clients;

import autotests.BaseTest;
import autotests.Payloads.WingState;
import com.consol.citrus.TestCaseRunner;
import io.qameta.allure.Step;
import org.springframework.http.HttpStatus;

import java.util.concurrent.atomic.AtomicInteger;

import static com.consol.citrus.DefaultTestActionBuilder.action;
import static com.consol.citrus.actions.ExecuteSQLAction.Builder.sql;
import static com.consol.citrus.actions.ExecuteSQLQueryAction.Builder.query;
import static com.consol.citrus.dsl.MessageSupport.MessageBodySupport.fromBody;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;


public class DuckActionClient extends BaseTest {
    @Step("Обновление базы данных")
    public void databaseUpdate(TestCaseRunner runner, String sql) {
        dbUpdate(runner, sql);
    }

    @Step("Проверяем уточку в базе данных")
    protected void validateDuckInDatabase(TestCaseRunner runner, String id, String color, String height,
                                          String material, String sound, String wingsState) {
        runner.$(query(testDb).statement("SELECT * FROM DUCK WHERE ID=" + id)
                .validate("COLOR", color)
                .validate("HEIGHT", height)
                .validate("MATERIAL", material)
                .validate("SOUND", sound)
                .validate("WINGS_STATE", wingsState));
    }

    @Step("Удаление уточки из базы данных")
    public void databaseDelete(TestCaseRunner runner) {
        runner.$(sql(testDb)
                .statement("DELETE FROM DUCK WHERE ID=" + "${id}"));
    }

    @Step("Создание уточки в базе данных")
   public void createDuckInDb(TestCaseRunner runner, String color, double height, String material, String sound,
                              WingState wingsState) {
        runner.$(sql(testDb).statement("insert into DUCK (id, color, height, material, sound, wings_state)\n" +
                "values (${id}, '" + color + "', " + height + ", '" + material + "', '" + sound + "'" +
                ",'" + wingsState + "');"));
   }

    //    создание уточки
    @Step("Эндпоинт для создания уточки")
    public void createDuck(TestCaseRunner runner, Object body) {
        postRequest(runner, duckService, "/api/duck/create", body);
    }

    // удаление уточки
    @Step("Эндпоинт для удаления уточки")
    public void duckDelete(TestCaseRunner runner) {
        deleteRequest(runner, duckService, "/api/duck/delete?id=${id}");
    }

    // обновление свойств уточки
    @Step("Эндпоинт для обновления свойств уточки")
    public void duckUpdate(TestCaseRunner runner, String color, double height, String material, String sound, WingState wingsState) {
        updateRequest(runner, duckService, "/api/duck/update", color, height, material, sound, wingsState);
    }

    // заставить уточку полетать
    @Step("Эндпоинт для того, чтобы заставить уточку полетать")
    public void duckFly(TestCaseRunner runner, String id) {
       getRequest(runner, duckService, "/api/duck/action/fly", id);
    }

    // заставить уточку крякать
    @Step("Эндпоинт для того, чтобы заставить уточку крякать")
    public void duckQuack(TestCaseRunner runner, String id, int repetitionCount, int soundCount) {
        quackRequest(runner, duckService, "/api/duck/action/quack", id, repetitionCount, soundCount);
    }

    // заставить уточку плавать
    @Step("Эндпоинт для того, чтобы заставить уточку плавать")
    public void duckSwim(TestCaseRunner runner, String id) {
        getRequest(runner, duckService, "/api/duck/action/swim", id);
    }

    // получение свойств уточки
    @Step("Эндпоинт для того, чтобы получить свойства уточки")
    public void duckProperties(TestCaseRunner runner, String id) {
        getRequest(runner, duckService, "/api/duck/action/properties", id);
    }

    @Step("Валидация с передачей ответа String'ой")
    public void validateResponse(TestCaseRunner runner, String expectedMessage, HttpStatus httpStatus) {
        validateResponseString(runner, duckService, expectedMessage, httpStatus);
    }

    @Step("Валидация с передачей ответа из папки resources")
    public void validateResponseFromResources(TestCaseRunner runner, String expectedPayload, HttpStatus httpStatus) {
        validateResponseResource(runner, duckService, expectedPayload, httpStatus);
    }

    @Step("Валидация с передачей ответа из папки Payloads")
    public void validateResponseFromPayloads(TestCaseRunner runner, Object expectedPayload, HttpStatus httpStatus) {
        validateResponsePayloads(runner, duckService, expectedPayload, httpStatus);
    }

    @Step("Проверка все ли свойства такие, какие нужны по задаче")
    public void validateProperties(TestCaseRunner runner, String color, String height, String material, String sound, String wingsState, HttpStatus httpStatus) {
        validateDuckProperties(runner, duckService, httpStatus, color, height, material, sound, wingsState);
    }

    @Step("Извлечение id для всех методов")
    public AtomicInteger extractId(TestCaseRunner runner) {
        AtomicInteger id = new AtomicInteger();
        runner.$(http().client(duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .extract(fromBody().expression("$.id", "duckId")));
        runner.$(action(context -> {
            id.set(context.getVariable("${duckId}", int.class));
        }));
        return id;
    }

    @Step("Получение рандомного id (условие true = для четного id, условие false = для нечетного id")
    public AtomicInteger getRandomId(TestCaseRunner runner, boolean isEven) {
        AtomicInteger id = new AtomicInteger();
        do {
            runner.variable("id", "citrus:randomNumber(3,true)");
            runner.$(action(context -> {
                id.set(context.getVariable("${id}", int.class));
            }));
        } while (isEven ? id.get() % 2 != 0 : id.get() % 2 == 0);
        return id;
    }

    @Step("Извлечение id и проверка ответа")
    public void validateCreateAndGetId(TestCaseRunner runner, HttpStatus httpStatus, String responseMessage) {
        validateResponseCreateAndGetId(runner, duckService, httpStatus, responseMessage);
    }
}
