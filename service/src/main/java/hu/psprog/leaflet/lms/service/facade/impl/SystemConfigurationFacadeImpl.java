package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.failover.api.client.FailoverClient;
import hu.psprog.leaflet.failover.api.domain.StatusResponse;
import hu.psprog.leaflet.lms.service.domain.system.Container;
import hu.psprog.leaflet.lms.service.domain.system.DockerRegistryContent;
import hu.psprog.leaflet.lms.service.domain.system.DockerRepository;
import hu.psprog.leaflet.lms.service.domain.system.SEOConfiguration;
import hu.psprog.leaflet.lms.service.facade.SystemConfigurationFacade;
import hu.psprog.leaflet.lms.service.facade.adapter.dcp.impl.SEOConfigurationDCPAdapter;
import hu.psprog.leaflet.lms.service.facade.client.StackAdminServiceClient;
import hu.psprog.leaflet.tlp.api.client.TLPClient;
import hu.psprog.leaflet.tlp.api.domain.LogEventPage;
import hu.psprog.leaflet.tlp.api.domain.LogRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link SystemConfigurationFacade}.
 *
 * @author Peter Smith
 */
@Service
public class SystemConfigurationFacadeImpl implements SystemConfigurationFacade {

    private final SEOConfigurationDCPAdapter seoConfigurationDCPAdapter;
    private final FailoverClient failoverClient;
    private final StackAdminServiceClient stackAdminServiceClient;
    private final TLPClient tlpClient;

    @Autowired
    public SystemConfigurationFacadeImpl(SEOConfigurationDCPAdapter seoConfigurationDCPAdapter, FailoverClient failoverClient,
                                         StackAdminServiceClient stackAdminServiceClient, TLPClient tlpClient) {
        this.seoConfigurationDCPAdapter = seoConfigurationDCPAdapter;
        this.failoverClient = failoverClient;
        this.stackAdminServiceClient = stackAdminServiceClient;
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
    public List<Container> getExistingContainers() {
        return stackAdminServiceClient.getExistingContainers();
    }

    @Override
    public Map<String, String> getConfiguredRegistries() {
        return stackAdminServiceClient.getConfiguredRegistries();
    }

    @Override
    public DockerRegistryContent getDockerRepositories(String registryID) {
        return stackAdminServiceClient.getDockerRepositories(registryID);
    }

    @Override
    public DockerRepository getDockerRepositoryDetails(String registryID, String repositoryID) {
        return stackAdminServiceClient.getDockerRepositoryTags(registryID, repositoryID);
    }

    @Override
    public void deleteDockerImageByTag(String registryID, String repositoryID, String tag) {
        stackAdminServiceClient.deleteDockerImageByTag(registryID, repositoryID, tag);
    }

    @Override
    public LogEventPage getLogs(LogRequest logRequest) throws CommunicationFailureException {
        return tlpClient.getLogs(logRequest);
    }

    @Override
    public LogEventPage getLogs(String logRequest) throws CommunicationFailureException {
        return tlpClient.getLogs(logRequest);
    }
}
