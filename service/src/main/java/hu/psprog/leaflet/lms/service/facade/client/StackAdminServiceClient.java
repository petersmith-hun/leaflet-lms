package hu.psprog.leaflet.lms.service.facade.client;

import hu.psprog.leaflet.lms.service.domain.dashboard.RegisteredServices;
import hu.psprog.leaflet.lms.service.domain.system.Container;

import java.util.List;

/**
 * Client for Stack Admin Service (LSAS).
 *
 * @author Peter Smith
 */
public interface StackAdminServiceClient {

    /**
     * Acquires list of registered services from LSAS.
     *
     * @return list of registered services
     */
    RegisteredServices getRegisteredServices();

    /**
     * Acquires list of existing Docker containers from LSAS.
     *
     * @return list of existing Docker containers
     */
    List<Container> getExistingContainers();
}
