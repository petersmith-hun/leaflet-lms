package hu.psprog.leaflet.lms.service.facade;

import java.util.List;

/**
 * Facade for dashboard operations.
 *
 * @author Peter Smith
 */
public interface DashboardFacade {

    /**
     * Retrieves the list of abbreviations of registered services.
     *
     * @return list of abbreviations
     */
    List<String> getRegisteredServices();
}
