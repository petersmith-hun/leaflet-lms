package hu.psprog.leaflet.lms.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Main application context configuration.
 *
 * @author Peter Smith
 */
@Configuration
@ComponentScan(ApplicationContextConfig.COMPONENT_SCAN)
public class ApplicationContextConfig {

    static final String COMPONENT_SCAN = "hu.psprog.leaflet";
}
