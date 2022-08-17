package hexlet.code.app.config;

import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.Config;
import com.rollbar.spring.webmvc.RollbarSpringConfigBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration()
@ComponentScan({
        // ADD YOUR PROJECT PACKAGE HERE
        "hexlet.code.app"
})
public class RollbarConfig {

    @Value("${rollbar_token:}")
    private String rollbarToken;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    /**
     * Register a Rollbar bean to configure App with Rollbar.
     */
    @Bean
    public Rollbar rollbar() throws Exception {
        Rollbar rollbar = Rollbar.init(getRollbarConfigs(rollbarToken));
        rollbar.log("App started");
        rollbar.close(true);
        return rollbar;
    }

    private Config getRollbarConfigs(final String accessToken) {

        // Reference ConfigBuilder.java for all the properties you can set for Rollbar
        return RollbarSpringConfigBuilder.withAccessToken(accessToken)
                .environment(activeProfile)
                .enabled(activeProfile.equals("production"))
                .build();
    }
}
