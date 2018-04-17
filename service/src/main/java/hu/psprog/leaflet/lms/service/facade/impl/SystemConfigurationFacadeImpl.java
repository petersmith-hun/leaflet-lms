package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.failover.api.client.FailoverClient;
import hu.psprog.leaflet.failover.api.domain.StatusResponse;
import hu.psprog.leaflet.lms.service.domain.system.SEOConfiguration;
import hu.psprog.leaflet.lms.service.facade.SystemConfigurationFacade;
import hu.psprog.leaflet.lms.service.facade.adapter.dcp.impl.SEOConfigurationDCPAdapter;
import hu.psprog.leaflet.tlp.api.client.TLPClient;
import hu.psprog.leaflet.tlp.api.domain.LogEventPage;
import hu.psprog.leaflet.tlp.api.domain.LogRequest;
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
    public StatusResponse getFailoverStatus() throws CommunicationFailureException {
        return failoverClient.getFailoverStatus();
    }

    @Override
    public LogEventPage getLogs(LogRequest logRequest) throws CommunicationFailureException {
        return tlpClient.getLogs(logRequest);
    }
}
