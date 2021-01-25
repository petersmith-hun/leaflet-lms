package hu.psprog.leaflet.lms.service.facade;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.failover.api.domain.StatusResponse;
import hu.psprog.leaflet.lms.service.domain.system.Container;
import hu.psprog.leaflet.lms.service.domain.system.DockerRegistryContent;
import hu.psprog.leaflet.lms.service.domain.system.DockerRepository;
import hu.psprog.leaflet.lms.service.domain.system.SEOConfiguration;
import hu.psprog.leaflet.tlp.api.domain.LogEventPage;
import hu.psprog.leaflet.tlp.api.domain.LogRequest;

import java.util.List;
import java.util.Map;

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
     * Calls LSAS existing containers endpoint to return list of existing Docker containers.
     *
     * @return existing Docker containers as {@link List} of {@link Container} objects.
     */
    List<Container> getExistingContainers();

    /**
     * Calls LSAS list configured Docker registries endpoint.
     *
     * @return configured registries as {@link Map} of registry ID - URL pairs
     */
    Map<String, String> getConfiguredRegistries();

    /**
     * Calls LSAS list Docker repositories endpoint for the given registry.
     *
     * @param registryID ID of the registry to be listed
     * @return registry contents as {@link DockerRegistryContent} object
     */
    DockerRegistryContent getDockerRepositories(String registryID);

    /**
     * Calls LSAS Docker repository details endpoint.
     *
     * @param registryID ID of the registry to be queried
     * @param repositoryID ID of the repository to be detailed
     * @return repository contents as {@link DockerRepository} object
     */
    DockerRepository getDockerRepositoryDetails(String registryID, String repositoryID);

    /**
     * Calls LSAS image deletion endpoint.
     *
     * @param registryID ID of the registry to be managed
     * @param repositoryID ID of the repository to be managed
     * @param tag tag of the image to be deleted
     */
    void deleteDockerImageByTag(String registryID, String repositoryID, String tag);

    /**
     * Calls TLP logs endpoint for retrieving stored logs.
     *
     * @param logRequest log retrieval paging and filtering settings as {@link LogRequest} object
     * @return response of TLP application containing paged list of logs
     * @throws CommunicationFailureException if LMS fails to reach TLP
     */
    LogEventPage getLogs(LogRequest logRequest) throws CommunicationFailureException;
}
