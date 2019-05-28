package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.lms.service.config.StackStatusConfigModel;
import hu.psprog.leaflet.lms.service.domain.dashboard.RegisteredServices;
import hu.psprog.leaflet.lms.service.facade.DashboardFacade;
import hu.psprog.leaflet.lms.service.facade.client.StackAdminServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link DashboardFacade}.
 * Calls LSAS to acquire the list of registered services.
 *
 * @author Peter Smith
 */
@Service
public class DashboardFacadeImpl implements DashboardFacade {

    private StackAdminServiceClient stackAdminServiceClient;
    private StackStatusConfigModel stackStatusConfigModel;

    @Autowired
    public DashboardFacadeImpl(StackAdminServiceClient stackAdminServiceClient, StackStatusConfigModel stackStatusConfigModel) {
        this.stackAdminServiceClient = stackAdminServiceClient;
        this.stackStatusConfigModel = stackStatusConfigModel;
    }

    @Override
    public List<String> getRegisteredServices() {

        return stackStatusConfigModel.isEnabled()
                ? getListOfRegisteredServices()
                : Collections.emptyList();
    }

    private List<String> getListOfRegisteredServices() {
        return Optional.ofNullable(stackAdminServiceClient.getRegisteredServices())
                .map(RegisteredServices::getRegisteredServices)
                .orElse(Collections.emptyList());
    }
}
