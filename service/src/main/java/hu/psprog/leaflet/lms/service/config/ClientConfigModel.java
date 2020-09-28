package hu.psprog.leaflet.lms.service.config;

/**
 * Common interface for client configuration models.
 *
 * @author Peter Smith
 */
public interface ClientConfigModel {

    /**
     * Returns the configured default service endpoint.
     *
     * @return the configured default service endpoint
     */
    String getDefaultEndpoint();

    /**
     * Returns the configured API key for the service.
     *
     * @return the configured API key
     */
    String getApiKey();
}
