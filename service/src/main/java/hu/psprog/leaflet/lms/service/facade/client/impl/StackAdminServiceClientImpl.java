package hu.psprog.leaflet.lms.service.facade.client.impl;

import hu.psprog.leaflet.lms.service.config.ClientConfigModel;
import hu.psprog.leaflet.lms.service.config.DockerClusterStatusConfigModel;
import hu.psprog.leaflet.lms.service.config.StackStatusConfigModel;
import hu.psprog.leaflet.lms.service.domain.dashboard.RegisteredServices;
import hu.psprog.leaflet.lms.service.domain.system.Container;
import hu.psprog.leaflet.lms.service.domain.system.DockerRegistryContent;
import hu.psprog.leaflet.lms.service.domain.system.DockerRepository;
import hu.psprog.leaflet.lms.service.facade.client.StackAdminServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.SyncInvoker;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Jersey-based implementation of {@link StackAdminServiceClient}.
 *
 * @author Peter Smith
 */
@Component
public class StackAdminServiceClientImpl implements StackAdminServiceClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(StackAdminServiceClientImpl.class);

    private static final GenericType<List<Container>> CONTAINER_LIST_GENERIC_TYPE = new GenericType<>() {};
    private static final GenericType<Map<String, String>> REGISTRY_MAP_GENERIC_TYPE = new GenericType<>() {};
    private static final GenericType<DockerRegistryContent> DOCKER_REGISTRY_CONTENT_GENERIC_TYPE = new GenericType<>() {};
    private static final GenericType<DockerRepository> DOCKER_REPOSITORY_GENERIC_TYPE = new GenericType<>() {};
    private static final GenericType<Void> VOID_GENERIC_TYPE = new GenericType<>() {};

    private static final String X_API_KEY_HEADER = "X-Api-Key";
    private static final DockerRegistryContent FALLBACK_DOCKER_REGISTRY_CONTENT =
            new DockerRegistryContent("unknown", Collections.emptyList());
    private static final DockerRepository FALLBACK_DOCKER_REPOSITORY =
            new DockerRepository("unknown", "unknown", Collections.emptyList());

    private final Client client;
    private final StackStatusConfigModel stackStatusConfigModel;
    private final DockerClusterStatusConfigModel dockerClusterStatusConfigModel;

    @Autowired
    public StackAdminServiceClientImpl(Client client, StackStatusConfigModel stackStatusConfigModel,
                                       DockerClusterStatusConfigModel dockerClusterStatusConfigModel) {
        this.client = client;
        this.stackStatusConfigModel = stackStatusConfigModel;
        this.dockerClusterStatusConfigModel = dockerClusterStatusConfigModel;
    }

    @Override
    public RegisteredServices getRegisteredServices() {

        RegisteredServices registeredServices = null;

        try {
            Response response = callLSAS(stackStatusConfigModel);
            if (isSuccessful(response)) {
                registeredServices = response.readEntity(RegisteredServices.class);
            } else {
                LOGGER.error("Response status returned by LSAS is {}", response.getStatus());
            }
            response.close();
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve list of registered services.", e);
        }

        return registeredServices;
    }

    @Override
    public List<Container> getExistingContainers() {

        List<Container> runningContainers = Collections.emptyList();

        try {
            Response response = callLSAS(dockerClusterStatusConfigModel);
            if (isSuccessful(response)) {
                runningContainers = response.readEntity(CONTAINER_LIST_GENERIC_TYPE);
            } else {
                LOGGER.error("Response status returned by LSAS is {}", response.getStatus());
            }
            response.close();
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve list of running containers.", e);
        }

        return runningContainers;
    }

    @Override
    public Map<String, String> getConfiguredRegistries() {

        return callDockerRegistryBrowser(stackStatusConfigModel.getDockerRepositoryBrowserEndpoint(), REGISTRY_MAP_GENERIC_TYPE, Collections.emptyMap());
    }

    @Override
    public DockerRegistryContent getDockerRepositories(String registryID) {

        String endpoint = createEndpoint(stackStatusConfigModel.getDockerRepositoryBrowserEndpoint(), registryID);

        return callDockerRegistryBrowser(endpoint, DOCKER_REGISTRY_CONTENT_GENERIC_TYPE, FALLBACK_DOCKER_REGISTRY_CONTENT);
    }

    @Override
    public DockerRepository getDockerRepositoryTags(String registryID, String repositoryID) {

        String endpoint = createEndpoint(stackStatusConfigModel.getDockerRepositoryBrowserEndpoint(), registryID, repositoryID);

        return callDockerRegistryBrowser(endpoint, DOCKER_REPOSITORY_GENERIC_TYPE, FALLBACK_DOCKER_REPOSITORY);
    }

    @Override
    public void deleteDockerImageByTag(String registryID, String repositoryID, String tag) {

        String endpoint = createEndpoint(stackStatusConfigModel.getDockerRepositoryBrowserEndpoint(), registryID, repositoryID, tag);

        callDockerRegistryBrowser(endpoint, VOID_GENERIC_TYPE, null, SyncInvoker::delete);
    }

    private String createEndpoint(String... endpointParts) {
        return String.join("/", endpointParts);
    }

    private <T> T callDockerRegistryBrowser(String endpoint, GenericType<T> returnType, T fallbackValue) {
        return callDockerRegistryBrowser(endpoint, returnType, fallbackValue, SyncInvoker::get);
    }

    private <T> T callDockerRegistryBrowser(String endpoint, GenericType<T> returnType, T fallbackValue, Function<Invocation.Builder, Response> requestMethodFunction) {

        T returnValue = fallbackValue;
        try {
            Response response = callLSAS(endpoint, stackStatusConfigModel.getApiKey(), requestMethodFunction);
            if (isSuccessful(response)) {
                if (!VOID_GENERIC_TYPE.equals(returnType)) {
                    returnValue = response.readEntity(returnType);
                }
            } else {
                LOGGER.error("Response status returned by LSAS is {}", response.getStatus());
            }
            response.close();
        } catch (Exception e) {
            LOGGER.error("Docker Registry Browser request failed", e);
        }

        return returnValue;
    }

    private Response callLSAS(ClientConfigModel clientConfig) {

        return callLSAS(clientConfig.getDefaultEndpoint(), clientConfig.getApiKey(), SyncInvoker::get);
    }

    private Response callLSAS(String endpoint, String apiKey, Function<Invocation.Builder, Response> requestMethodFunction) {

        Invocation.Builder invocationBuilder = client.target(endpoint)
                .request()
                .header(X_API_KEY_HEADER, apiKey);

        return requestMethodFunction.apply(invocationBuilder);
    }

    private boolean isSuccessful(Response response) {
        return response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
    }
}
