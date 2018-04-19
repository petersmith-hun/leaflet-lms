package hu.psprog.leaflet.lms.service.facade;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.failover.api.domain.StatusResponse;
import hu.psprog.leaflet.lms.service.domain.system.SEOConfiguration;
import hu.psprog.leaflet.tlp.api.domain.LogEventPage;
import hu.psprog.leaflet.tlp.api.domain.LogRequest;

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

    /**
     * Calls CBFS status endpoint for retrieving failover status information.
     *
     * @return failover status information as {@link StatusResponse}
     * @throws CommunicationFailureException if LMS fails to reach CBFS
     */
    StatusResponse getFailoverStatus() throws CommunicationFailureException;

    /**
     * Calls TLP logs endpoint for retrieving stored logs.
     *
     * @param logRequest log retrieval paging and filtering settings as {@link LogRequest} object
     * @return response of TLP application containing paged list of logs
     * @throws CommunicationFailureException if LMS fails to reach TLP
     */
    LogEventPage getLogs(LogRequest logRequest) throws CommunicationFailureException;
}
