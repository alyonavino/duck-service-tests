package autotests.duckController;

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

public class DuckDeleteTest extends DuckActionClient {
    @Test(description = "Проверка того, что удалили уточку")
    @CitrusTest
    public void successfulDelete(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner,"yellow", 10.0, "rubber", "quack", "ACTIVE");
        String id = extractId(runner).toString();
        duckDelete(runner,id);
        validateResponse(runner,HttpStatus.OK,"{\n" + " \"message\": \"Duck is deleted\"\n" + "}");
    }
}

