package hu.psprog.leaflet.lms.service.config;

import hu.psprog.leaflet.bridge.oauth.support.SpringIntegratedOAuthRequestAuthentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

/**
 * Additional Spring configuration for service layer.
 *
 * @author Peter Smith
 */
@Configuration
public class ServiceConfiguration {

    private static final String JS_CLIENT_REGISTRATION_ID = "lsas-js";

    @Bean
    public SpringIntegratedOAuthRequestAuthentication stackAdminJSClientRequestAuthentication(ClientRegistrationRepository clientRegistrationRepository,
                                                                                              OAuth2AuthorizedClientManager authorizedClientServiceOAuth2AuthorizedClientManager) {

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(JS_CLIENT_REGISTRATION_ID);

        return new SpringIntegratedOAuthRequestAuthentication(clientRegistration,
                builder -> builder.principal(JS_CLIENT_REGISTRATION_ID),
                authorizedClientServiceOAuth2AuthorizedClientManager);
    }
}
