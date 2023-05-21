package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.lms.service.config.StackStatusConfigModel;
import hu.psprog.leaflet.lms.service.domain.dashboard.RegisteredServices;
import hu.psprog.leaflet.lms.service.domain.dashboard.StackStatusJSClientHelperModel;
import hu.psprog.leaflet.lms.service.facade.DashboardFacade;
import hu.psprog.leaflet.lms.service.facade.client.StackAdminServiceClient;
import hu.psprog.leaflet.lms.service.facade.client.factory.JSClientHelperModelFactory;
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

    private final StackAdminServiceClient stackAdminServiceClient;
    private final StackStatusConfigModel stackStatusConfigModel;
    private final JSClientHelperModelFactory<StackStatusJSClientHelperModel> jsClientHelperModelFactory;

    @Autowired
    public DashboardFacadeImpl(StackAdminServiceClient stackAdminServiceClient, StackStatusConfigModel stackStatusConfigModel,
                               JSClientHelperModelFactory<StackStatusJSClientHelperModel> jsClientHelperModelFactory) {
        this.stackAdminServiceClient = stackAdminServiceClient;
        this.stackStatusConfigModel = stackStatusConfigModel;
        this.jsClientHelperModelFactory = jsClientHelperModelFactory;
    }

    @Override
    public List<String> getRegisteredServices() {

        return stackStatusConfigModel.isEnabled()
                ? getListOfRegisteredServices()
                : Collections.emptyList();
    }

    @Override
    public StackStatusJSClientHelperModel getJSClientHelperModel() {
        return jsClientHelperModelFactory.getJSClientHelperModel();
    }

    private List<String> getListOfRegisteredServices() {
        return Optional.ofNullable(stackAdminServiceClient.getRegisteredServices())
                .map(RegisteredServices::registeredServices)
                .orElse(Collections.emptyList());
    }
}
