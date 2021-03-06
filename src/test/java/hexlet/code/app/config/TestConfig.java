package hexlet.code.app.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.TestPropertySource;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.EnableAspectJAutoProxy;
//import org.springframework.context.annotation.PropertySource;


@Configuration
@Profile(TestConfig.TEST_PROFILE)
@ComponentScan(basePackages = "hexlet.code.app")
@TestPropertySource(value = "classpath:/config/test.yml")
public class TestConfig {

    public static final String TEST_PROFILE = "test";

//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplateBuilder().build();
//    }
}
