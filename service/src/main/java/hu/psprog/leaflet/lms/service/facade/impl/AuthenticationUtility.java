package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.lms.service.auth.JWTTokenAuthentication;
import hu.psprog.leaflet.lms.service.auth.handler.JWTTokenPayloadReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Authentication utilities.
 *
 * @author Peter Smith
 */
@Component
class AuthenticationUtility {

    private JWTTokenPayloadReader jwtTokenPayloadReader;

    @Autowired
    public AuthenticationUtility(JWTTokenPayloadReader jwtTokenPayloadReader) {
        this.jwtTokenPayloadReader = jwtTokenPayloadReader;
    }

    /**
     * Creates temporal {@link JWTTokenAuthentication} object (without username).
     * Can be used for creating temporal authentication for password reset.
     *
     * @param token token to include in {@link JWTTokenAuthentication} object
     * @return populated {@link JWTTokenAuthentication} object
     */
    Authentication createTemporal(String token) {
        return new JWTTokenAuthentication.Builder()
                .withDetails(jwtTokenPayloadReader.readPayload(token))
                .withToken(token)
                .build();
    }

    /**
     * Creates and immediately stores temporal {@link JWTTokenAuthentication} object (without username) in SecurityContext.
     * Can be used for creating temporal authentication for password reset.
     *
     * @param token token to include in {@link JWTTokenAuthentication} object
     */
    void createAndStoreTemporal(String token) {
        store(createTemporal(token));
    }

    /**
     * Replaces current {@link JWTTokenAuthentication} object with the given new one.
     * Can be used for session extension.
     *
     * @param username username to include in {@link JWTTokenAuthentication} object
     * @param token token to include in {@link JWTTokenAuthentication} object
     */
    void replace(String username, String token) {

        Authentication authentication = new JWTTokenAuthentication.Builder()
                .withEmailAddress(username)
                .withDetails(jwtTokenPayloadReader.readPayload(token))
                .withToken(token)
                .build();
        store(authentication);
    }

    /**
     * Stores given {@link Authentication} object in SecurityContext.
     *
     * @param authentication {@link Authentication} object to store
     */
    void store(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
