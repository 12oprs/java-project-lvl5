package hexlet.code.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

class AppApplicationTests {

    @Test
    void testInit() {
        assertThat(true).isTrue();
    }
}
