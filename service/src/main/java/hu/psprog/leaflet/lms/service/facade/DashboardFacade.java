package hu.psprog.leaflet.lms.service.facade;

import hu.psprog.leaflet.lms.service.domain.dashboard.StackStatusJSClientHelperModel;

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

    /**
     * Returns a JavaScript client helper object for stack status monitoring.
     *
     * @return JS client helper object of type {@link StackStatusJSClientHelperModel}
     */
    StackStatusJSClientHelperModel getJSClientHelperModel();
}
