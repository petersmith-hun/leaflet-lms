package hu.psprog.leaflet.lms.service.config;

import hu.psprog.leaflet.bridge.oauth.support.SpringIntegratedOAuthRequestAuthentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

/**
 * Additional Spring configuration for service layer.
 *
 * @author Peter Smith
 */
@Configuration
public class ServiceConfiguration {

    private static final String JS_CLIENT_REGISTRATION_ID = "lsas-js";

    @Bean
    public SpringIntegratedOAuthRequestAuthentication stackAdminJSClientRequestAuthentication(OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager) {
        return new SpringIntegratedOAuthRequestAuthentication(JS_CLIENT_REGISTRATION_ID, oAuth2AuthorizedClientManager);
    }
}
