package hu.psprog.leaflet.lms.service.facade;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.system.SEOConfiguration;
import hu.psprog.leaflet.lms.service.domain.system.failover.StatusResponse;
import hu.psprog.leaflet.lms.service.domain.tlp.LogEventPage;
import hu.psprog.leaflet.lms.service.domain.tlp.LogRequest;
import hu.psprog.leaflet.lms.service.exception.FailoverCommunicationException;
import hu.psprog.leaflet.lms.service.exception.TLPCommunicationException;

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
     * @throws FailoverCommunicationException if LMS fails to reach CBFS
     */
    StatusResponse getFailoverStatus() throws FailoverCommunicationException;

    /**
     * Calls TLP logs endpoint for retrieving stored logs.
     *
     * @param logRequest log retrieval paging and filtering settings as {@link LogRequest} object
     * @return response of TLP application containing paged list of logs
     * @throws TLPCommunicationException if LMS fails to reach TLP
     */
    LogEventPage getLogs(LogRequest logRequest) throws TLPCommunicationException;
}
