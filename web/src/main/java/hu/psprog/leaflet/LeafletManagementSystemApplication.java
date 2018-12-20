package hu.psprog.leaflet;

import hu.psprog.leaflet.lms.web.config.EmbeddedWebServerAJPCustomization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

/**
 * Spring Boot entry point.
 *
 * @author Peter Smith
 */
@SpringBootApplication
public class LeafletManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeafletManagementSystemApplication.class, args);
    }

    @Bean
    @Profile("production")
    public EmbeddedWebServerAJPCustomization ajpContainerCustomizer(@Value("${tomcat.ajp.port}") int ajpPort) {
        return new EmbeddedWebServerAJPCustomization(ajpPort);
    }
}
