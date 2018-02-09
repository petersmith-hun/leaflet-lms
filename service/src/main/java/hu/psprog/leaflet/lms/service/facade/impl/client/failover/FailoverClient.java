package hu.psprog.leaflet.lms.service.facade.impl.client.failover;

import hu.psprog.leaflet.lms.service.exception.FailoverCommunicationException;

import javax.ws.rs.core.Response;

/**
 * Jersey REST client for CBFS failover application.
 *
 * @author Peter Smith
 */
public interface FailoverClient {

    /**
     * Makes an HTTP request for failover status retrieval.
     *
     * @return response of failover status endpoint as {@link Response}
     * @throws FailoverCommunicationException when LMS fails to reach failover application
     */
    Response getFailoverStatus() throws FailoverCommunicationException;
}
