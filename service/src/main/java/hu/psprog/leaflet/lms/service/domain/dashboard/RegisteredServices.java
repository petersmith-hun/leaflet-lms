package hu.psprog.leaflet.lms.service.domain.dashboard;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/**
 * Domain class containing the list of registered services.
 *
 * @author Peter Smith
 */
public record RegisteredServices(List<String> registeredServices) {

    @JsonCreator
    public RegisteredServices {
    }

}
