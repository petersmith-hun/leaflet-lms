package hu.psprog.leaflet.lms.service.domain.system;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * Domain class representing a Docker container's base information set.
 *
 * @author Peter Smith
 */
@JsonDeserialize(builder = Container.ContainerBuilder.class)
public class Container {

    private final String id;
    private final String image;
    private final List<String> names;
    private final String state;

    private Container(String id, String image, List<String> names, String state) {
        this.id = id;
        this.image = image;
        this.names = names;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public List<String> getNames() {
        return names;
    }

    public String getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Container container = (Container) o;

        return new EqualsBuilder()
                .append(id, container.id)
                .append(image, container.image)
                .append(names, container.names)
                .append(state, container.state)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(image)
                .append(names)
                .append(state)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("image", image)
                .append("names", names)
                .append("state", state)
                .toString();
    }

    public static ContainerBuilder getBuilder() {
        return new ContainerBuilder();
    }

    @JsonPOJOBuilder
    public static final class ContainerBuilder {
        private String id;
        private String image;
        private List<String> names;
        private String state;

        private ContainerBuilder() {
        }

        public ContainerBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public ContainerBuilder withImage(String image) {
            this.image = image;
            return this;
        }

        public ContainerBuilder withNames(List<String> names) {
            this.names = names;
            return this;
        }

        public ContainerBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public Container build() {
            return new Container(id, image, names, state);
        }
    }
}
