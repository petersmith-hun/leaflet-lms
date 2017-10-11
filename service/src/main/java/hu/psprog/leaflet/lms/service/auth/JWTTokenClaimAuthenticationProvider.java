package hu.psprog.leaflet.lms.service.auth;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.exception.TokenAuthenticationFailureException;
import hu.psprog.leaflet.lms.service.facade.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Standard Spring Security {@link AuthenticationProvider} implementation which handles JWT based authentication for LMS.
 * This provider calls token claim service under Leaflet backend application through Bridge.
 *
 * @author Peter Smith
 */
@Component
public class JWTTokenClaimAuthenticationProvider implements AuthenticationProvider {

    private static final String BRIDGE_COULD_NOT_REACH_LEAFLET = "Bridge could not reach Leaflet backend application for authentication.";

    private UserFacade userFacade;

    @Autowired
    public JWTTokenClaimAuthenticationProvider(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        try {
            return userFacade.claimToken(authentication);
        } catch (CommunicationFailureException e) {
            throw new TokenAuthenticationFailureException(BRIDGE_COULD_NOT_REACH_LEAFLET, e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {

        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
