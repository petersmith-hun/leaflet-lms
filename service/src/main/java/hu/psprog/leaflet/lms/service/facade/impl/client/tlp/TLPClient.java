package hu.psprog.leaflet.lms.service.facade.impl.client.tlp;

import hu.psprog.leaflet.lms.service.domain.tlp.LogRequest;
import hu.psprog.leaflet.lms.service.exception.TLPCommunicationException;

import javax.ws.rs.core.Response;

/**
 * Jersey REST client for Tiny Log Processor application.
 *
 * @author Peter Smith
 */
public interface TLPClient {

    /**
     * Makes an HTTP request for TLP logs retrieval.
     *
     * @param logRequest log retrieval paging and filtering settings as {@link LogRequest} object
     * @return response of TLP logs endpoint as {@link Response}
     * @throws TLPCommunicationException when LMS fails to reach TLP application
     */
    Response getLogs(LogRequest logRequest) throws TLPCommunicationException;
}
