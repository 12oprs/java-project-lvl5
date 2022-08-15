package hexlet.code.app.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.TestPropertySource;


@Configuration
@Profile(TestConfig.TEST_PROFILE)
@ComponentScan(basePackages = "hexlet.code")
@TestPropertySource(value = "classpath:/config/test.yml")
public class TestConfig {

    public static final String TEST_PROFILE = "test";

}
