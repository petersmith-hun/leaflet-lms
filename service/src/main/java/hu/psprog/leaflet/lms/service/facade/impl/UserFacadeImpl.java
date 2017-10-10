package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.user.LoginRequestModel;
import hu.psprog.leaflet.api.rest.request.user.PasswordResetDemandRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserPasswordRequestModel;
import hu.psprog.leaflet.api.rest.response.user.LoginResponseDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.UserBridgeService;
import hu.psprog.leaflet.lms.service.auth.JWTTokenAuthentication;
import hu.psprog.leaflet.lms.service.auth.handler.JWTTokenPayloadReader;
import hu.psprog.leaflet.lms.service.exception.TokenAuthenticationFailureException;
import hu.psprog.leaflet.lms.service.facade.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Implementation of {@link UserFacade}.
 *
 * @author Peter Smith
 */
@Service
public class UserFacadeImpl implements UserFacade {

    private static final String INVALID_USER_CREDENTIALS = "Invalid user credentials.";

    private UserBridgeService userBridgeService;
    private JWTTokenPayloadReader jwtTokenPayloadReader;
    private AuthenticationUtility authenticationUtility;

    @Autowired
    public UserFacadeImpl(UserBridgeService userBridgeService, JWTTokenPayloadReader jwtTokenPayloadReader, AuthenticationUtility authenticationUtility) {
        this.userBridgeService = userBridgeService;
        this.jwtTokenPayloadReader = jwtTokenPayloadReader;
        this.authenticationUtility = authenticationUtility;
    }

    @Override
    public void demandPasswordReset(PasswordResetDemandRequestModel passwordResetDemandRequestModel) throws CommunicationFailureException {
        userBridgeService.demandPasswordReset(passwordResetDemandRequestModel);
    }

    @Override
    public void confirmPasswordReset(UserPasswordRequestModel userPasswordRequestModel, String token) throws CommunicationFailureException {
        authenticationUtility.createAndStoreTemporal(token);
        userBridgeService.confirmPasswordReset(userPasswordRequestModel);
    }

    @Override
    public void renewToken(Authentication authentication) throws CommunicationFailureException {
        authenticationUtility.replace(authentication.getPrincipal().toString(), userBridgeService.renewToken().getToken());
    }

    @Override
    public void revokeToken() throws CommunicationFailureException {
        userBridgeService.revokeToken();
    }

    @Override
    public Authentication claimToken(Authentication authentication) throws CommunicationFailureException {

        LoginResponseDataModel loginResponseModel = userBridgeService.claimToken(convertToLoginRequest(authentication));

        if (Objects.isNull(loginResponseModel) || loginResponseModel.getStatus() != LoginResponseDataModel.AuthenticationResult.AUTH_SUCCESS) {
            throw new TokenAuthenticationFailureException(INVALID_USER_CREDENTIALS);
        }

        return new JWTTokenAuthentication.Builder()
                .withEmailAddress(authentication.getPrincipal().toString())
                .withToken(loginResponseModel.getToken())
                .withDetails(jwtTokenPayloadReader.readPayload(loginResponseModel.getToken()))
                .build();
    }

    private LoginRequestModel convertToLoginRequest(Authentication authentication) {

        LoginRequestModel loginRequestModel = new LoginRequestModel();
        loginRequestModel.setEmail(authentication.getPrincipal().toString());
        loginRequestModel.setPassword(authentication.getCredentials().toString());

        return loginRequestModel;
    }
}
