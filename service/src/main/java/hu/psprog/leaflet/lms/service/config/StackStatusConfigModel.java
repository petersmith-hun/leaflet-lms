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
public class StackStatusConfigModel {

    private boolean enabled;
    private String registeredServicesEndpoint;
    private String discoverEndpoint;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        StackStatusConfigModel that = (StackStatusConfigModel) o;

        return new EqualsBuilder()
                .append(enabled, that.enabled)
                .append(registeredServicesEndpoint, that.registeredServicesEndpoint)
                .append(discoverEndpoint, that.discoverEndpoint)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(enabled)
                .append(registeredServicesEndpoint)
                .append(discoverEndpoint)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("enabled", enabled)
                .append("registeredServicesEndpoint", registeredServicesEndpoint)
                .append("discoverEndpoint", discoverEndpoint)
                .toString();
    }
}
