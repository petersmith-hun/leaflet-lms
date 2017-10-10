package hu.psprog.leaflet.lms.web.auth;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.auth.JWTTokenAuthentication;
import hu.psprog.leaflet.lms.service.auth.user.AuthenticationUserDetailsModel;
import hu.psprog.leaflet.lms.service.facade.UserFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Filter to handle session extension.
 * Checks if the currently used JWT-token based authentication object is about to expire (threshold is configurable, shall be provided in minutes).
 * On success, token is replaced in the security context.
 *
 * @author Peter Smith
 */
@Component
@ConfigurationProperties(prefix = "session.auto-renew", ignoreUnknownFields = false)
public class SessionExtensionFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionExtensionFilter.class);

    private UserFacade userFacade;

    private boolean enabled;
    private int threshold;

    @Autowired
    public SessionExtensionFilter(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (enabled) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (isExpiringSoon(authentication)) {
                try {
                    userFacade.renewToken(authentication);
                } catch (CommunicationFailureException e) {
                    LOGGER.error("Leaflet unreachable - failed to renew user session.", e);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Sets if session extension filter is enabled.
     *
     * @param enabled status
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Sets expiration threshold in minutes.
     *
     * @param threshold expiration threshold in minutes
     */
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    private boolean isExpiringSoon(Authentication authentication) {

        boolean expiringSoon = false;
        if (authentication instanceof JWTTokenAuthentication) {
            Date expirationDate = ((AuthenticationUserDetailsModel) authentication.getDetails()).getExpiration();
            long difference = expirationDate.getTime() - System.currentTimeMillis();
            expiringSoon = TimeUnit.MILLISECONDS.toMinutes(difference) <= threshold;
        }

        return expiringSoon;
    }
}
