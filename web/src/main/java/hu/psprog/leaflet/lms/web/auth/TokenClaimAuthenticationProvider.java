package hu.psprog.leaflet.lms.web.auth;

import hu.psprog.leaflet.api.rest.request.user.LoginRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.UserBridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * @author Peter Smith
 */
@Component
public class TokenClaimAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserBridgeService userBridgeService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        try {
            userBridgeService.claimToken(convertToLoginRequest(authentication));
        } catch (CommunicationFailureException e) {

            // log + throw authexception
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {

        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private LoginRequestModel convertToLoginRequest(Authentication authentication) {

        return new LoginRequestModel(authentication.getPrincipal().toString(),
                authentication.getCredentials().toString());
    }
}
