package hu.psprog.leaflet.lms.service.facade.client.factory.impl;

import hu.psprog.leaflet.bridge.oauth.support.SpringIntegratedOAuthRequestAuthentication;
import hu.psprog.leaflet.lms.service.config.DockerClusterStatusConfigModel;
import hu.psprog.leaflet.lms.service.domain.dashboard.DockerStatusJSClientHelperModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link DockerStatusJSClientHelperModelFactory}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class DockerStatusJSClientHelperModelFactoryTest {

    private static final boolean IS_ENABLED = true;
    private static final String DETAILS_ENDPOINT = "http://localhost:9999/details";
    private static final String STATUS_ENDPOINT = "http://localhost:9999/status";
    private static final Map<String, String> AUTHORIZATION_HEADER = Map.of("Authorization", "Bearer token");

    @Mock
    private SpringIntegratedOAuthRequestAuthentication springIntegratedOAuthRequestAuthentication;

    @Mock
    private DockerClusterStatusConfigModel dockerClusterStatusConfigModel;

    @InjectMocks
    private DockerStatusJSClientHelperModelFactory dockerStatusJSClientHelperModelFactory;

    @Test
    public void shouldGetJSClientHelperModelReturnStackStatusModel() {

        // given
        given(dockerClusterStatusConfigModel.isEnabled()).willReturn(IS_ENABLED);
        given(dockerClusterStatusConfigModel.getDetailsEndpoint()).willReturn(DETAILS_ENDPOINT);
        given(dockerClusterStatusConfigModel.getStatusEndpoint()).willReturn(STATUS_ENDPOINT);
        given(springIntegratedOAuthRequestAuthentication.getAuthenticationHeader()).willReturn(AUTHORIZATION_HEADER);

        // when
        DockerStatusJSClientHelperModel result = dockerStatusJSClientHelperModelFactory.getJSClientHelperModel();

        // then
        assertThat(result, notNullValue());
        assertThat(result.enabled(), is(IS_ENABLED));
        assertThat(result.detailsEndpoint(), equalTo(DETAILS_ENDPOINT));
        assertThat(result.statusEndpoint(), equalTo(STATUS_ENDPOINT));
        assertThat(result.authorization(), equalTo(AUTHORIZATION_HEADER));
    }
}
