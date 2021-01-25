package hu.psprog.leaflet.lms.service.facade.client;

import hu.psprog.leaflet.lms.service.domain.dashboard.RegisteredServices;
import hu.psprog.leaflet.lms.service.domain.system.Container;
import hu.psprog.leaflet.lms.service.domain.system.DockerRegistryContent;
import hu.psprog.leaflet.lms.service.domain.system.DockerRepository;

import java.util.List;
import java.util.Map;

/**
 * Client for Stack Admin Service (LSAS).
 *
 * @author Peter Smith
 */
public interface StackAdminServiceClient {

    /**
     * Acquires list of registered services from LSAS.
     *
     * @return list of registered services
     */
    RegisteredServices getRegisteredServices();

    /**
     * Acquires list of existing Docker containers from LSAS.
     *
     * @return list of existing Docker containers
     */
    List<Container> getExistingContainers();

    /**
     * Acquires list of configured Docker registries.
     *
     * @return configured registries as {@link Map} of registry ID - URL pairs
     */
    Map<String, String> getConfiguredRegistries();

    /**
     * Acquires list of Docker repositories for the given registry.
     *
     * @param registryID ID of the registry to be listed
     * @return registry contents as {@link DockerRegistryContent} object
     */
    DockerRegistryContent getDockerRepositories(String registryID);

    /**
     * Acquires Docker repository details.
     *
     * @param registryID ID of the registry to be queried
     * @param repositoryID ID of the repository to be detailed
     * @return repository contents as {@link DockerRepository} object
     */
    DockerRepository getDockerRepositoryTags(String registryID, String repositoryID);

    /**
     * Requests Docker image deletion.
     *
     * @param registryID ID of the registry to be managed
     * @param repositoryID ID of the repository to be managed
     * @param tag tag of the image to be deleted
     */
    void deleteDockerImageByTag(String registryID, String repositoryID, String tag);
}
