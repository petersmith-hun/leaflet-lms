package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.system.SEOConfiguration;
import hu.psprog.leaflet.lms.service.facade.SystemConfigurationFacade;
import hu.psprog.leaflet.lms.service.facade.adapter.dcp.impl.SEOConfigurationDCPAdapter;
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

    @Autowired
    public SystemConfigurationFacadeImpl(SEOConfigurationDCPAdapter seoConfigurationDCPAdapter) {
        this.seoConfigurationDCPAdapter = seoConfigurationDCPAdapter;
    }

    @Override
    public SEOConfiguration getCurrentSEOConfiguration() throws CommunicationFailureException {
        return seoConfigurationDCPAdapter.collect();
    }

    @Override
    public void processUpdateSEOConfiguration(SEOConfiguration seoConfiguration) throws CommunicationFailureException {
        seoConfigurationDCPAdapter.update(seoConfiguration);
    }
}
