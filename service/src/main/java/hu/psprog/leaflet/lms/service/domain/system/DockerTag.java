package hu.psprog.leaflet.lms.service.domain.system;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.ZonedDateTime;

/**
 * Domain class representing a Docker image tag.
 *
 * @author Peter Smith
 */
public class DockerTag {

    private final String name;
    private final ZonedDateTime created;

    public DockerTag(String name, ZonedDateTime created) {
        this.name = name;
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DockerTag dockerTag = (DockerTag) o;

        return new EqualsBuilder()
                .append(name, dockerTag.name)
                .append(created, dockerTag.created)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(created)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("created", created)
                .toString();
    }
}
