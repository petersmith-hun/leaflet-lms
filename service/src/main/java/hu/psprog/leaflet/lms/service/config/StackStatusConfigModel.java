package hu.psprog.leaflet.lms.service.config;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Component for stack status configuration parameters.
 *
 * @author Peter Smith
 */
@Component
@ConfigurationProperties(prefix = "stack-status")
public class StackStatusConfigModel implements ClientConfigModel {

    private boolean enabled;
    private String apiKey;
    private String registeredServicesEndpoint;
    private String discoverEndpoint;
    private String dockerRepositoryBrowserEndpoint;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getDefaultEndpoint() {
        return registeredServicesEndpoint;
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getRegisteredServicesEndpoint() {
        return registeredServicesEndpoint;
    }

    public void setRegisteredServicesEndpoint(String registeredServicesEndpoint) {
        this.registeredServicesEndpoint = registeredServicesEndpoint;
    }

    public String getDiscoverEndpoint() {
        return discoverEndpoint;
    }

    public void setDiscoverEndpoint(String discoverEndpoint) {
        this.discoverEndpoint = discoverEndpoint;
    }

    public String getDockerRepositoryBrowserEndpoint() {
        return dockerRepositoryBrowserEndpoint;
    }

    public void setDockerRepositoryBrowserEndpoint(String dockerRepositoryBrowserEndpoint) {
        this.dockerRepositoryBrowserEndpoint = dockerRepositoryBrowserEndpoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        StackStatusConfigModel that = (StackStatusConfigModel) o;

        return new EqualsBuilder()
                .append(enabled, that.enabled)
                .append(apiKey, that.apiKey)
                .append(registeredServicesEndpoint, that.registeredServicesEndpoint)
                .append(discoverEndpoint, that.discoverEndpoint)
                .append(dockerRepositoryBrowserEndpoint, that.dockerRepositoryBrowserEndpoint)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(enabled)
                .append(apiKey)
                .append(registeredServicesEndpoint)
                .append(discoverEndpoint)
                .append(dockerRepositoryBrowserEndpoint)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("enabled", enabled)
                .append("apiKey", apiKey)
                .append("registeredServicesEndpoint", registeredServicesEndpoint)
                .append("discoverEndpoint", discoverEndpoint)
                .append("dockerRepositoryBrowserEndpoint", dockerRepositoryBrowserEndpoint)
                .toString();
    }
}
