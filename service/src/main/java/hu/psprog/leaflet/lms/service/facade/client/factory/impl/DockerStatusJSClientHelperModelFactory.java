package hu.psprog.leaflet.lms.service.facade.client.factory.impl;

import hu.psprog.leaflet.bridge.oauth.support.SpringIntegratedOAuthRequestAuthentication;
import hu.psprog.leaflet.lms.service.config.DockerClusterStatusConfigModel;
import hu.psprog.leaflet.lms.service.domain.dashboard.DockerStatusJSClientHelperModel;
import hu.psprog.leaflet.lms.service.facade.client.factory.JSClientHelperModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * {@link JSClientHelperModelFactory} implementation for generating {@link DockerStatusJSClientHelperModel} instances.
 *
 * @author Peter Smith
 */
@Component
public class DockerStatusJSClientHelperModelFactory implements JSClientHelperModelFactory<DockerStatusJSClientHelperModel> {

    private final SpringIntegratedOAuthRequestAuthentication stackAdminJSClientRequestAuthentication;
    private final DockerClusterStatusConfigModel dockerClusterStatusConfigModel;

    @Autowired
    public DockerStatusJSClientHelperModelFactory(SpringIntegratedOAuthRequestAuthentication stackAdminJSClientRequestAuthentication,
                                                  DockerClusterStatusConfigModel dockerClusterStatusConfigModel) {
        this.stackAdminJSClientRequestAuthentication = stackAdminJSClientRequestAuthentication;
        this.dockerClusterStatusConfigModel = dockerClusterStatusConfigModel;
    }

    @Override
    public DockerStatusJSClientHelperModel getJSClientHelperModel() {

        return DockerStatusJSClientHelperModel.builder()
                .enabled(dockerClusterStatusConfigModel.isEnabled())
                .detailsEndpoint(dockerClusterStatusConfigModel.getDetailsEndpoint())
                .statusEndpoint(dockerClusterStatusConfigModel.getStatusEndpoint())
                .authorization(stackAdminJSClientRequestAuthentication.getAuthenticationHeader())
                .build();
    }
}
