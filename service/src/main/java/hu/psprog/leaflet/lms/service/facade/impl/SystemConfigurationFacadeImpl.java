package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.system.SEOConfiguration;
import hu.psprog.leaflet.lms.service.domain.system.failover.StatusResponse;
import hu.psprog.leaflet.lms.service.exception.FailoverCommunicationException;
import hu.psprog.leaflet.lms.service.facade.SystemConfigurationFacade;
import hu.psprog.leaflet.lms.service.facade.adapter.dcp.impl.SEOConfigurationDCPAdapter;
import hu.psprog.leaflet.lms.service.facade.impl.client.failover.FailoverClient;
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

    @Autowired
    public SystemConfigurationFacadeImpl(SEOConfigurationDCPAdapter seoConfigurationDCPAdapter, FailoverClient failoverClient) {
        this.seoConfigurationDCPAdapter = seoConfigurationDCPAdapter;
        this.failoverClient = failoverClient;
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
}
