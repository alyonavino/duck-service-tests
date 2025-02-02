package autotests;

import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class EndpointConfig {
    @Bean("duckService")
    public HttpClient duckService() {
        return new HttpClientBuilder()
                .requestUrl("http://localhost:2222")
                .build();
    }
    @Bean("testDb")
    public SingleConnectionDataSource db() {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:tcp://localhost:9092/mem:ducks");
        dataSource.setUsername("dev");
        dataSource.setPassword("dev");
        return dataSource;
    }
    @Autowired
    protected SingleConnectionDataSource testDb;
}
