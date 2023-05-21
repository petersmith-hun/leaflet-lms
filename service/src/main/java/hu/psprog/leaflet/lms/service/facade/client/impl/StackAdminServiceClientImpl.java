package hu.psprog.leaflet.lms.service.facade.client.impl;

import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.domain.BridgeService;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import hu.psprog.leaflet.lms.service.domain.dashboard.RegisteredServices;
import hu.psprog.leaflet.lms.service.domain.system.Container;
import hu.psprog.leaflet.lms.service.domain.system.DockerRegistryContent;
import hu.psprog.leaflet.lms.service.domain.system.DockerRepository;
import hu.psprog.leaflet.lms.service.facade.client.StackAdminServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.ws.rs.core.GenericType;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Jersey-based implementation of {@link StackAdminServiceClient}.
 *
 * @author Peter Smith
 */
@BridgeService(client = "lsas")
public class StackAdminServiceClientImpl implements StackAdminServiceClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(StackAdminServiceClientImpl.class);

    private static final GenericType<List<Container>> CONTAINER_LIST_GENERIC_TYPE = new GenericType<>() {};
    private static final GenericType<Map<String, String>> REGISTRY_MAP_GENERIC_TYPE = new GenericType<>() {};

    private static final String PATH_PARAMETER_REGISTRY_ID = "registryID";
    private static final String PATH_PARAMETER_GROUP_ID = "groupID";
    private static final String PATH_PARAMETER_REPOSITORY_ID = "repositoryID";
    private static final String PATH_PARAMETER_TAG_ID = "tagID";

    private final BridgeClient bridgeClient;

    @Autowired
    public StackAdminServiceClientImpl(BridgeClient bridgeClient) {
        this.bridgeClient = bridgeClient;
    }

    @Override
    public RegisteredServices getRegisteredServices() {

        RESTRequest request = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(LSASPath.REGISTERED_SERVICES)
                .authenticated()
                .build();

        return safeCall(() -> bridgeClient.call(request, RegisteredServices.class),
                () -> "Failed to retrieve list of registered services", null);
    }

    @Override
    public List<Container> getExistingContainers() {

        RESTRequest request = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(LSASPath.CONTAINERS)
                .authenticated()
                .build();

        return safeCall(() -> bridgeClient.call(request, CONTAINER_LIST_GENERIC_TYPE),
                () -> "Failed to retrieve list of running containers", Collections.emptyList());
    }

    @Override
    public Map<String, String> getConfiguredRegistries() {

        RESTRequest request = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(LSASPath.REGISTRY)
                .authenticated()
                .build();

        return safeCall(() -> bridgeClient.call(request, REGISTRY_MAP_GENERIC_TYPE),
                () -> "Failed to retrieve configured registries", Collections.emptyMap());
    }

    @Override
    public DockerRegistryContent getDockerRepositories(String registryID) {

        RESTRequest request = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(LSASPath.REGISTRY_REPOSITORIES)
                .addPathParameter(PATH_PARAMETER_REGISTRY_ID, registryID)
                .authenticated()
                .build();

        return safeCall(() -> bridgeClient.call(request, DockerRegistryContent.class),
                () -> String.format("Failed to retrieve repositories in registry=%s", registryID),
                DockerRegistryContent.FALLBACK_DOCKER_REGISTRY_CONTENT);
    }

    @Override
    public DockerRepository getDockerRepositoryTags(String registryID, String repositoryID) {

        RepositoryID repository = new RepositoryID(repositoryID);

        RESTRequest request = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(repository.useGroupPath
                        ? LSASPath.REGISTRY_GROUPED_REPOSITORIES_TAGS
                        : LSASPath.REGISTRY_REPOSITORIES_TAGS)
                .addPathParameter(PATH_PARAMETER_REGISTRY_ID, registryID)
                .addPathParameter(PATH_PARAMETER_GROUP_ID, repository.groupID)
                .addPathParameter(PATH_PARAMETER_REPOSITORY_ID, repository.repositoryID)
                .authenticated()
                .build();

        return safeCall(() -> bridgeClient.call(request, DockerRepository.class),
                () -> String.format("Failed to retrieve tags of repository=%s in registry=%s", repositoryID, registryID),
                DockerRepository.FALLBACK_DOCKER_REPOSITORY);
    }

    @Override
    public void deleteDockerImageByTag(String registryID, String repositoryID, String tag) {

        RepositoryID repository = new RepositoryID(repositoryID);

        RESTRequest request = RESTRequest.getBuilder()
                .method(RequestMethod.DELETE)
                .path(repository.useGroupPath
                        ? LSASPath.REGISTRY_GROUPED_REPOSITORIES_TAGS_TAG
                        : LSASPath.REGISTRY_REPOSITORIES_TAGS_TAG)
                .addPathParameter(PATH_PARAMETER_REGISTRY_ID, registryID)
                .addPathParameter(PATH_PARAMETER_GROUP_ID, repository.groupID)
                .addPathParameter(PATH_PARAMETER_REPOSITORY_ID, repository.repositoryID)
                .addPathParameter(PATH_PARAMETER_TAG_ID, tag)
                .authenticated()
                .build();

        safeCall(() -> bridgeClient.call(request),
                () -> String.format("Failed to delete tag=%s of repository=%s in registry=%s", tag, repositoryID, registryID));
    }

    private <T> T safeCall(BridgeSupplier<T> bridgeCall, Supplier<String> fallbackMessageSupplier, T fallbackValue) {

        T returnValue;
        try {
            returnValue = bridgeCall.get();
        } catch (Exception e) {
            returnValue = fallbackValue;
            LOGGER.error(fallbackMessageSupplier.get(), e);
        }

        return returnValue;
    }

    private void safeCall(BridgeRunnable bridgeCall, Supplier<String> fallbackMessageSupplier) {

        try {
            bridgeCall.run();
        } catch (Exception e) {
            LOGGER.error(fallbackMessageSupplier.get(), e);
        }
    }

    interface BridgeSupplier<T> {
        T get() throws CommunicationFailureException;
    }

    interface BridgeRunnable {
        void run() throws CommunicationFailureException;
    }

    static class RepositoryID {

        private final boolean useGroupPath;
        private final String groupID;
        private final String repositoryID;

        RepositoryID(String repositoryID) {

            String[] repositoryIDParts = repositoryID.split("/");
            if (repositoryIDParts.length == 2) {
                this.useGroupPath = true;
                this.groupID = repositoryIDParts[0];
                this.repositoryID = repositoryIDParts[1];
            } else {
                this.useGroupPath = false;
                this.groupID = null;
                this.repositoryID = repositoryID;
            }
        }
    }
}
