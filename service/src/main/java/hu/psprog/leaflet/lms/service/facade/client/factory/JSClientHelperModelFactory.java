package hu.psprog.leaflet.lms.service.facade.client.factory;

import hu.psprog.leaflet.lms.service.domain.dashboard.JSClientHelperModel;

/**
 * Factory interface for generating {@link JSClientHelperModel} instances.
 * The generated helper model instance can be used to pass the necessary information to a UI side HTTP client,
 * dealing with LSAS monitoring calls.
 *
 * @author Peter Smith
 */
public interface JSClientHelperModelFactory<T extends JSClientHelperModel> {

    /**
     * Generates a specific {@link JSClientHelperModel} instance for LSAS calls from the UI.
     *
     * @return generated helper model of an implementing type of {@link JSClientHelperModel}
     */
    T getJSClientHelperModel();
}
