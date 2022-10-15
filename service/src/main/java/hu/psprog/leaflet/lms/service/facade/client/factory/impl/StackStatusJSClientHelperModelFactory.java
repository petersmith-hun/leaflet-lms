package hu.psprog.leaflet.lms.service.facade.client.factory.impl;

import hu.psprog.leaflet.bridge.oauth.support.SpringIntegratedOAuthRequestAuthentication;
import hu.psprog.leaflet.lms.service.config.StackStatusConfigModel;
import hu.psprog.leaflet.lms.service.domain.dashboard.StackStatusJSClientHelperModel;
import hu.psprog.leaflet.lms.service.facade.client.factory.JSClientHelperModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * {@link JSClientHelperModelFactory} implementation for generating {@link StackStatusJSClientHelperModel} instances.
 *
 * @author Peter Smith
 */
@Component
public class StackStatusJSClientHelperModelFactory implements JSClientHelperModelFactory<StackStatusJSClientHelperModel> {

    private final SpringIntegratedOAuthRequestAuthentication stackAdminJSClientRequestAuthentication;
    private final StackStatusConfigModel stackStatusConfigModel;

    @Autowired
    public StackStatusJSClientHelperModelFactory(SpringIntegratedOAuthRequestAuthentication stackAdminJSClientRequestAuthentication,
                                                 StackStatusConfigModel stackStatusConfigModel) {
        this.stackAdminJSClientRequestAuthentication = stackAdminJSClientRequestAuthentication;
        this.stackStatusConfigModel = stackStatusConfigModel;
    }

    @Override
    public StackStatusJSClientHelperModel getJSClientHelperModel() {

        return StackStatusJSClientHelperModel.builder()
                .enabled(stackStatusConfigModel.isEnabled())
                .discoverEndpoint(stackStatusConfigModel.getDiscoverEndpoint())
                .authorization(stackAdminJSClientRequestAuthentication.getAuthenticationHeader())
                .build();
    }
}
