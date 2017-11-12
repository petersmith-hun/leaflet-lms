package hu.psprog.leaflet.lms.service.facade;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.system.SEOConfiguration;

/**
 * System configuration facade.
 * Various system-wide configuration adapters.
 *
 * @author Peter Smith
 */
public interface SystemConfigurationFacade {

    /**
     * Retrieves current SEO configuration.
     *
     * @return SEO values wrapped as {@link SEOConfiguration}
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    SEOConfiguration getCurrentSEOConfiguration() throws CommunicationFailureException;

    /**
     * Processes SEO configuration update request.
     *
     * @param seoConfiguration updated SEO values as {@link SEOConfiguration}
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    void processUpdateSEOConfiguration(SEOConfiguration seoConfiguration) throws CommunicationFailureException;
}
