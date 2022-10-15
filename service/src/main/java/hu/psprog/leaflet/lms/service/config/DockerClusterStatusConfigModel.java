package hu.psprog.leaflet.lms.service.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Component for Docker cluster status configuration parameters.
 *
 * @author Peter Smith
 */
@Component
@ConfigurationProperties(prefix = "docker-cluster-status")
@Data
@Setter(AccessLevel.PACKAGE)
public class DockerClusterStatusConfigModel {

    private boolean enabled;
    private String statusEndpoint;
    private String detailsEndpoint;
}
