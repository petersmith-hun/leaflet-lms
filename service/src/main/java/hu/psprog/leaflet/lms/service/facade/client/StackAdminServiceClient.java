package hu.psprog.leaflet.lms.service.facade.client;

import hu.psprog.leaflet.lms.service.domain.dashboard.RegisteredServices;

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
}
