package hu.psprog.leaflet.lms.web.auth;

import hu.psprog.leaflet.lms.service.auth.JWTTokenAuthentication;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * Filter that forces the user to re-login when their session expires or gets invalidated on Leaflet's side.
 * User session gets invalidated if they are authenticated, though Leaflet returns with a response of status '401 Unauthorized'.
 * In this case, the SecurityContext will get cleared, session invalidated and session cookies deleted.
 *
 * @author Peter Smith
 */
@Component
@Order
public class ExpiredSessionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        filterChain.doFilter(request, response);

        if (isAuthenticated() && isRequestUnauthorized(response)) {
            forceInvalidateUserSession(request);
        }
    }

    private void forceInvalidateUserSession(HttpServletRequest request) {

        SecurityContextHolder.clearContext();
        request.getSession(false).invalidate();
        Stream.of(request.getCookies())
                .forEach(cookie -> cookie.setMaxAge(0));
    }

    private boolean isAuthenticated() {
        return isAuthenticatedByJWT(getAuthentication()) && getAuthentication().isAuthenticated();
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean isAuthenticatedByJWT(Authentication authentication) {
        return authentication instanceof JWTTokenAuthentication;
    }

    private boolean isRequestUnauthorized(HttpServletResponse response) {
        return response.getStatus() == HttpStatus.UNAUTHORIZED.value();
    }
}
