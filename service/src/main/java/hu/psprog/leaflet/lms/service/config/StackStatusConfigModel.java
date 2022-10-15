package hu.psprog.leaflet.lms.service.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Component for stack status configuration parameters.
 *
 * @author Peter Smith
 */
@Component
@ConfigurationProperties(prefix = "stack-status")
@Data
@Setter(AccessLevel.PACKAGE)
public class StackStatusConfigModel {

    private boolean enabled;
    private String discoverEndpoint;
}
