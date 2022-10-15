package hu.psprog.leaflet.lms.service.facade.client.factory.impl;

import hu.psprog.leaflet.bridge.oauth.support.SpringIntegratedOAuthRequestAuthentication;
import hu.psprog.leaflet.lms.service.config.StackStatusConfigModel;
import hu.psprog.leaflet.lms.service.domain.dashboard.StackStatusJSClientHelperModel;
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
 * Unit tests for {@link StackStatusJSClientHelperModelFactory}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class StackStatusJSClientHelperModelFactoryTest {

    private static final boolean IS_ENABLED = true;
    private static final String DISCOVER_ENDPOINT = "http://localhost:9999/discover";
    private static final Map<String, String> AUTHORIZATION_HEADER = Map.of("Authorization", "Bearer token");

    @Mock
    private SpringIntegratedOAuthRequestAuthentication springIntegratedOAuthRequestAuthentication;

    @Mock
    private StackStatusConfigModel stackStatusConfigModel;

    @InjectMocks
    private StackStatusJSClientHelperModelFactory stackStatusJSClientHelperModelFactory;

    @Test
    public void shouldGetJSClientHelperModelReturnStackStatusModel() {

        // given
        given(stackStatusConfigModel.isEnabled()).willReturn(IS_ENABLED);
        given(stackStatusConfigModel.getDiscoverEndpoint()).willReturn(DISCOVER_ENDPOINT);
        given(springIntegratedOAuthRequestAuthentication.getAuthenticationHeader()).willReturn(AUTHORIZATION_HEADER);

        // when
        StackStatusJSClientHelperModel result = stackStatusJSClientHelperModelFactory.getJSClientHelperModel();

        // then
        assertThat(result, notNullValue());
        assertThat(result.isEnabled(), is(IS_ENABLED));
        assertThat(result.getDiscoverEndpoint(), equalTo(DISCOVER_ENDPOINT));
        assertThat(result.getAuthorization(), equalTo(AUTHORIZATION_HEADER));
    }
}
