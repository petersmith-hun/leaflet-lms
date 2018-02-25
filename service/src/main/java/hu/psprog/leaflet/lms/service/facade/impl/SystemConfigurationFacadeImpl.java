package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.system.SEOConfiguration;
import hu.psprog.leaflet.lms.service.domain.system.failover.StatusResponse;
import hu.psprog.leaflet.lms.service.domain.tlp.LogEventPage;
import hu.psprog.leaflet.lms.service.domain.tlp.LogRequest;
import hu.psprog.leaflet.lms.service.exception.FailoverCommunicationException;
import hu.psprog.leaflet.lms.service.exception.TLPCommunicationException;
import hu.psprog.leaflet.lms.service.facade.SystemConfigurationFacade;
import hu.psprog.leaflet.lms.service.facade.adapter.dcp.impl.SEOConfigurationDCPAdapter;
import hu.psprog.leaflet.lms.service.facade.impl.client.failover.FailoverClient;
import hu.psprog.leaflet.lms.service.facade.impl.client.tlp.TLPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link SystemConfigurationFacade}.
 *
 * @author Peter Smith
 */
@Service
public class SystemConfigurationFacadeImpl implements SystemConfigurationFacade {

    private SEOConfigurationDCPAdapter seoConfigurationDCPAdapter;
    private FailoverClient failoverClient;
    private TLPClient tlpClient;

    @Autowired
    public SystemConfigurationFacadeImpl(SEOConfigurationDCPAdapter seoConfigurationDCPAdapter, FailoverClient failoverClient, TLPClient tlpClient) {
        this.seoConfigurationDCPAdapter = seoConfigurationDCPAdapter;
        this.failoverClient = failoverClient;
        this.tlpClient = tlpClient;
    }

    @Override
    public SEOConfiguration getCurrentSEOConfiguration() throws CommunicationFailureException {
        return seoConfigurationDCPAdapter.collect();
    }

    @Override
    public void processUpdateSEOConfiguration(SEOConfiguration seoConfiguration) throws CommunicationFailureException {
        seoConfigurationDCPAdapter.update(seoConfiguration);
    }

    @Override
    public StatusResponse getFailoverStatus() throws FailoverCommunicationException {
        return failoverClient
                .getFailoverStatus()
                .readEntity(StatusResponse.class);
    }

    @Override
    public LogEventPage getLogs(LogRequest logRequest) throws TLPCommunicationException {
        return tlpClient
                .getLogs(logRequest)
                .readEntity(LogEventPage.class);
    }
}
