package hu.psprog.leaflet.lms.web.auth;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.facade.UserFacade;
import hu.psprog.leaflet.lms.web.exception.CouldNotReachBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Logout handler to revoke JWT authentication token on logout.
 *
 * @author Peter Smith
 */
@Component
public class TokenRevokeLogoutHandler implements LogoutHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenRevokeLogoutHandler.class);
    private static final String BRIDGE_COULD_NOT_REACH_LEAFLET = "Bridge could not reach Leaflet backend application for authentication.";

    private UserFacade userFacade;

    @Autowired
    public TokenRevokeLogoutHandler(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        try {
            userFacade.revokeToken();
        } catch (CommunicationFailureException e) {
            LOGGER.error(BRIDGE_COULD_NOT_REACH_LEAFLET, e);
            throw new CouldNotReachBackendException(e);
        }
    }
}
