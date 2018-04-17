package hu.psprog.leaflet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

/**
 * Spring Boot entry point.
 *
 * @author Peter Smith
 */
@SpringBootApplication
public class LeafletManagementSystemApplication {

    private static final String AJP_PROTOCOL = "AJP/1.3";

    public static void main(String[] args) {
        SpringApplication.run(LeafletManagementSystemApplication.class, args);
    }

    @Bean
    @Profile("production")
    public EmbeddedServletContainerCustomizer ajpContainerCustomizer(@Value("${tomcat.ajp.port}") int ajpPort) {
        return configurableEmbeddedServletContainer -> {
            TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) configurableEmbeddedServletContainer;
            tomcat.setProtocol(AJP_PROTOCOL);
            tomcat.setPort(ajpPort);
        };
    }
}
