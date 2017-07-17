package hu.psprog.leaflet.lms.web.auth;

import hu.psprog.leaflet.api.rest.request.user.LoginRequestModel;
import hu.psprog.leaflet.api.rest.response.user.LoginResponseDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.UserBridgeService;
import hu.psprog.leaflet.lms.web.exception.TokenAuthenticationFailureException;
import hu.psprog.leaflet.lms.web.response.handler.JWTTokenPayloadReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Standard Spring Security {@link AuthenticationProvider} implementation which handles JWT based authentication for LMS.
 * This provider calls token claim service under Leaflet backend application through Bridge.
 *
 * @author Peter Smith
 */
@Component
public class JWTTokenClaimAuthenticationProvider implements AuthenticationProvider {

    private static final String BRIDGE_COULD_NOT_REACH_LEAFLET = "Bridge could not reach Leaflet backend application for authentication.";
    private static final String INVALID_USER_CREDENTIALS = "Invalid user credentials.";

    @Autowired
    private UserBridgeService userBridgeService;

    @Autowired
    private JWTTokenPayloadReader jwtTokenPayloadReader;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        try {
            LoginResponseDataModel loginResponseModel = userBridgeService.claimToken(convertToLoginRequest(authentication));

            if (Objects.isNull(loginResponseModel) || loginResponseModel.getStatus() != LoginResponseDataModel.AuthenticationResult.AUTH_SUCCESS) {
                throw new TokenAuthenticationFailureException(INVALID_USER_CREDENTIALS);
            }

            return new JWTTokenAuthentication.Builder()
                    .withEmailAddress(authentication.getPrincipal().toString())
                    .withToken(loginResponseModel.getToken())
                    .withDetails(jwtTokenPayloadReader.readPayload(loginResponseModel.getToken()))
                    .build();
        } catch (CommunicationFailureException e) {

            throw new TokenAuthenticationFailureException(BRIDGE_COULD_NOT_REACH_LEAFLET, e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {

        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private LoginRequestModel convertToLoginRequest(Authentication authentication) {

        LoginRequestModel loginRequestModel = new LoginRequestModel();
        loginRequestModel.setEmail(authentication.getPrincipal().toString());
        loginRequestModel.setPassword(authentication.getCredentials().toString());

        return loginRequestModel;
    }
}
