package hu.psprog.leaflet.lms.service.facade.client.impl;

import hu.psprog.leaflet.lms.service.config.DockerClusterStatusConfigModel;
import hu.psprog.leaflet.lms.service.config.StackStatusConfigModel;
import hu.psprog.leaflet.lms.service.domain.dashboard.RegisteredServices;
import hu.psprog.leaflet.lms.service.domain.system.Container;
import hu.psprog.leaflet.lms.service.facade.client.StackAdminServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

/**
 * Jersey-based implementation of {@link StackAdminServiceClient}.
 *
 * @author Peter Smith
 */
@Component
public class StackAdminServiceClientImpl implements StackAdminServiceClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(StackAdminServiceClientImpl.class);

    private static final GenericType<List<Container>> CONTAINER_LIST_GENERIC_TYPE = new GenericType<>() {};

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
            Response response = callLSAS(stackStatusConfigModel.getRegisteredServicesEndpoint());
            if (isSuccessful(response)) {
                registeredServices = response.readEntity(RegisteredServices.class);
            } else {
                LOGGER.error("Response status returned by LSAS is {}", response.getStatus());
            }
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve list of registered services.", e);
        }

        return registeredServices;
    }

    @Override
    public List<Container> getExistingContainers() {

        List<Container> runningContainers = Collections.emptyList();

        try {
            Response response = callLSAS(dockerClusterStatusConfigModel.getExistingContainersEndpoint());
            if (isSuccessful(response)) {
                runningContainers = response.readEntity(CONTAINER_LIST_GENERIC_TYPE);
            } else {
                LOGGER.error("Response status returned by LSAS is {}", response.getStatus());
            }
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve list of running containers.", e);
        }

        return runningContainers;
    }

    private Response callLSAS(String endpoint) {

        return client.target(endpoint)
                .request()
                .get();
    }

    private boolean isSuccessful(Response response) {
        return response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
    }
}
