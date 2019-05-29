package hu.psprog.leaflet.lms.service.domain.dashboard;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * Domain class containing the list of registered services.
 *
 * @author Peter Smith
 */
public class RegisteredServices {

    private List<String> registeredServices;

    @JsonCreator
    public RegisteredServices(List<String> registeredServices) {
        this.registeredServices = registeredServices;
    }

    public List<String> getRegisteredServices() {
        return registeredServices;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        RegisteredServices that = (RegisteredServices) o;

        return new EqualsBuilder()
                .append(registeredServices, that.registeredServices)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(registeredServices)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("registeredServices", registeredServices)
                .toString();
    }
}
