package hu.psprog.leaflet.lms.service.config;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Component for Docker cluster status configuration parameters.
 *
 * @author Peter Smith
 */
@Component
@ConfigurationProperties(prefix = "docker-cluster-status")
public class DockerClusterStatusConfigModel {

    private boolean enabled;
    private String existingContainersEndpoint;
    private String statusEndpoint;
    private String detailsEndpoint;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getExistingContainersEndpoint() {
        return existingContainersEndpoint;
    }

    public void setExistingContainersEndpoint(String existingContainersEndpoint) {
        this.existingContainersEndpoint = existingContainersEndpoint;
    }

    public String getStatusEndpoint() {
        return statusEndpoint;
    }

    public void setStatusEndpoint(String statusEndpoint) {
        this.statusEndpoint = statusEndpoint;
    }

    public String getDetailsEndpoint() {
        return detailsEndpoint;
    }

    public void setDetailsEndpoint(String detailsEndpoint) {
        this.detailsEndpoint = detailsEndpoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DockerClusterStatusConfigModel that = (DockerClusterStatusConfigModel) o;

        return new EqualsBuilder()
                .append(enabled, that.enabled)
                .append(existingContainersEndpoint, that.existingContainersEndpoint)
                .append(statusEndpoint, that.statusEndpoint)
                .append(detailsEndpoint, that.detailsEndpoint)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(enabled)
                .append(existingContainersEndpoint)
                .append(statusEndpoint)
                .append(detailsEndpoint)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("enabled", enabled)
                .append("existingContainersEndpoint", existingContainersEndpoint)
                .append("statusEndpoint", statusEndpoint)
                .append("detailsEndpoint", detailsEndpoint)
                .toString();
    }
}
