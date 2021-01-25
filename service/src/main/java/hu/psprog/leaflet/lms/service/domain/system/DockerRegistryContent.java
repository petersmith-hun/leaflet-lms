package hu.psprog.leaflet.lms.service.domain.system;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * Domain class representing a Docker containing the list of repositories in a registry.
 *
 * @author Peter Smith
 */
public class DockerRegistryContent {

    private final String registryName;
    private final List<String> repositories;

    public DockerRegistryContent(String registryName, List<String> repositories) {
        this.registryName = registryName;
        this.repositories = repositories;
    }

    public String getRegistryName() {
        return registryName;
    }

    public List<String> getRepositories() {
        return repositories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DockerRegistryContent that = (DockerRegistryContent) o;

        return new EqualsBuilder()
                .append(registryName, that.registryName)
                .append(repositories, that.repositories)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(registryName)
                .append(repositories)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("registryName", registryName)
                .append("repositories", repositories)
                .toString();
    }

}
