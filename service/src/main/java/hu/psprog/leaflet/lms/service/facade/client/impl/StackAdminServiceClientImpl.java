package hu.psprog.leaflet.lms.service.facade.client.impl;

import hu.psprog.leaflet.lms.service.config.StackStatusConfigModel;
import hu.psprog.leaflet.lms.service.domain.dashboard.RegisteredServices;
import hu.psprog.leaflet.lms.service.facade.client.StackAdminServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

/**
 * Jersey-based implementation of {@link StackAdminServiceClient}.
 *
 * @author Peter Smith
 */
@Component
public class StackAdminServiceClientImpl implements StackAdminServiceClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(StackAdminServiceClientImpl.class);

    private Client client;
    private StackStatusConfigModel stackStatusConfigModel;

    @Autowired
    public StackAdminServiceClientImpl(Client client, StackStatusConfigModel stackStatusConfigModel) {
        this.client = client;
        this.stackStatusConfigModel = stackStatusConfigModel;
    }

    @Override
    public RegisteredServices getRegisteredServices() {

        RegisteredServices registeredServices = null;

        try {
            Response response = callLSAS();
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

    private Response callLSAS() {

        return client.target(stackStatusConfigModel.getRegisteredServicesEndpoint())
                .request()
                .get();
    }

    private boolean isSuccessful(Response response) {
        return response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
    }
}
