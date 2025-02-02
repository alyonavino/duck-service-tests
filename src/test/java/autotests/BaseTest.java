package autotests;

import autotests.Payloads.WingState;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.config.CitrusSpringConfig;
import com.consol.citrus.dsl.JsonPathSupport;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.message.builder.ObjectMappingPayloadBuilder;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.context.ContextConfiguration;

import static com.consol.citrus.actions.ExecuteSQLAction.Builder.sql;
import static com.consol.citrus.actions.ExecuteSQLQueryAction.Builder.query;
import static com.consol.citrus.dsl.MessageSupport.MessageBodySupport.fromBody;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

@ContextConfiguration(classes = {EndpointConfig.class})
public class BaseTest extends TestNGCitrusSpringSupport {
    @Autowired
    protected HttpClient duckService;
    @Autowired
    protected SingleConnectionDataSource testDb;

    protected void dbUpdate(TestCaseRunner runner, String sql) {
        runner.$(sql(testDb)
                .statement(sql));
    }

    protected void deleteRequest(TestCaseRunner runner, HttpClient URL, String path) {
        runner.$(
                http()
                        .client(URL)
                        .send()
                        .delete(path));
    }


    protected  void postRequest(TestCaseRunner runner, HttpClient URL, String path, Object body) {
    runner.$(http().client(URL)
            .send()
            .post(path)
            .message()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new ObjectMappingPayloadBuilder(body, new ObjectMapper())));
    }

    protected void updateRequest(TestCaseRunner runner, HttpClient URL, String path, String color, double height, String material, String sound, WingState wingsState) {
        runner.$(http().client(URL)
                .send()
                .put(path)
                .queryParam("color", color)
                .queryParam("height", String.valueOf(height))
                .queryParam("material", material)
                .queryParam("sound", sound)
                .queryParam("wingsState", String.valueOf(wingsState))
                .queryParam("id", "${id}"));
    }

    protected void getRequest(TestCaseRunner runner, HttpClient URL, String path, String id) {
        runner.$(http().client(URL)
                .send()
                .get(path)
                .queryParam("id", id));
    }

    protected void quackRequest(TestCaseRunner runner, HttpClient URL, String path, String id, int repetitionCount, int soundCount) {
        runner.$(http().client(URL)
                .send()
                .get(path)
                .queryParam("id", id)
                .queryParam("repetitionCount", String.valueOf(repetitionCount))
                .queryParam("soundCount", String.valueOf(soundCount)));
    }

    protected void validateResponseString(TestCaseRunner runner, HttpClient URL, String expectedMessage, HttpStatus httpStatus) {
        runner.$(http().client(URL)
                .receive()
                .response(httpStatus)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(expectedMessage));
    }

    protected void validateResponseResource(TestCaseRunner runner, HttpClient URL, String expectedPayload, HttpStatus httpStatus) {
        runner.$(http().client(URL)
                .receive()
                .response(httpStatus)
                .message().type(MessageType.JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new ClassPathResource(expectedPayload)));
    }

    protected void validateResponsePayloads(TestCaseRunner runner, HttpClient URL, Object expectedPayload, HttpStatus httpStatus) {
        runner.$(http().client(URL)
                .receive()
                .response(httpStatus)
                .message().type(MessageType.JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new ObjectMappingPayloadBuilder(expectedPayload, new ObjectMapper())));
    }

    protected void validateDuckProperties(TestCaseRunner runner, HttpClient URL, HttpStatus httpStatus, String color, String height, String material, String sound, String wingsState) {
        runner.$(http().client(URL)
                .receive()
                .response(httpStatus)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .validate(JsonPathSupport.jsonPath().expression("$.color", color))
                .validate(JsonPathSupport.jsonPath().expression("$.height", height))
                .validate(JsonPathSupport.jsonPath().expression("$.material", material))
                .validate(JsonPathSupport.jsonPath().expression("$.sound", sound))
                .validate(JsonPathSupport.jsonPath().expression("$.wingsState", wingsState)));
    }

    protected void validateResponseCreateAndGetId(TestCaseRunner runner, HttpClient URL, HttpStatus httpStatus, String responseMessage) {
        runner.$(http().client(URL)
                .receive()
                .response(httpStatus)
                .message()
                .extract(fromBody().expression("$.id", "duckId"))
                .body(new ClassPathResource(responseMessage)));
    }
}



