package hu.psprog.leaflet.lms.web.response.handler;

import hu.psprog.leaflet.lms.web.response.model.user.AuthenticationUserDetailsModel;

/**
 * Extracts payload from a JWT token.
 *
 * @author Peter Smith
 */
public interface JWTTokenPayloadReader {

    /**
     * Extracts and parses JWT payload.
     *
     * @param token JWT token
     * @return JWT payload wrapped as {@link AuthenticationUserDetailsModel} object.
     */
    AuthenticationUserDetailsModel readPayload(String token);
}
