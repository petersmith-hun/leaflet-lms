package hu.psprog.leaflet.lms.service.facade;

import hu.psprog.leaflet.api.rest.request.user.PasswordResetDemandRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserPasswordRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import org.springframework.security.core.Authentication;

/**
 * User operations facade.
 *
 * @author Peter Smith
 */
public interface UserFacade {

    /**
     * Processes password reset request.
     *
     * @param passwordResetDemandRequestModel user's email address wrapped in {@link PasswordResetDemandRequestModel} to request password reset for
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    void demandPasswordReset(PasswordResetDemandRequestModel passwordResetDemandRequestModel) throws CommunicationFailureException;

    /**
     * Processes password reset confirmation.
     *
     * @param userPasswordRequestModel {@link UserPasswordRequestModel} holding the new password and its confirmation
     * @param token temporal password reset token
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    void confirmPasswordReset(UserPasswordRequestModel userPasswordRequestModel, String token) throws CommunicationFailureException;

    /**
     * Processes token renewal request for given email address.
     *
     * @param authentication current authentication object
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    void renewToken(Authentication authentication) throws CommunicationFailureException;

    /**
     * Processes logout (token revoke).
     *
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void revokeToken() throws CommunicationFailureException;

    /**
     * Processes token claim (login) request.
     *
     * @param authentication original username-password based authentication object
     * @return JWT based authentication object on successful authentication
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    Authentication claimToken(Authentication authentication) throws CommunicationFailureException;
}
