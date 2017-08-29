package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.user.PasswordResetDemandRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserPasswordRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.UserBridgeService;
import hu.psprog.leaflet.lms.web.auth.JWTTokenAuthentication;
import hu.psprog.leaflet.lms.web.response.handler.JWTTokenPayloadReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Peter Smith
 */
@Controller
public class AuthenticationController {

    private static final String LOGIN = "/login";
    private static final String PASSWORD_RESET = "/password-reset";
    private static final String PASSWORD_RESET_TOKEN = "/password-reset/{token:.+}";

    private UserBridgeService userBridgeService;
    private JWTTokenPayloadReader jwtTokenPayloadReader;

    @Autowired
    public AuthenticationController(UserBridgeService userBridgeService, JWTTokenPayloadReader jwtTokenPayloadReader) {
        this.userBridgeService = userBridgeService;
        this.jwtTokenPayloadReader = jwtTokenPayloadReader;
    }

    @RequestMapping(LOGIN)
    public String showLoginForm() {

        return "view/users/login";
    }

    @RequestMapping(PASSWORD_RESET)
    public String showPasswordResetDemandForm() {
        return "view/users/reclaim_demand";
    }

    @RequestMapping(PASSWORD_RESET_TOKEN)
    public String showPasswordResetConfirmationForm() {
        return "view/users/reclaim_confirm";
    }

    @RequestMapping(method = RequestMethod.POST, path = PASSWORD_RESET)
    public String processPasswordResetDemand(@ModelAttribute PasswordResetDemandRequestModel passwordResetDemandRequestModel) throws Exception {
        try {
            userBridgeService.demandPasswordReset(passwordResetDemandRequestModel);
        } catch (CommunicationFailureException e) {
            throw new Exception(e);
        }
        return "redirect:/password-reset";
    }

    @RequestMapping(method = RequestMethod.POST, path = PASSWORD_RESET_TOKEN)
    public String processPasswordResetConfirmation(@ModelAttribute UserPasswordRequestModel userPasswordRequestModel,
                                                   @PathVariable("token") String token) throws Exception {
        setTemporalAuthenticationForPasswordReset(token);
        try {
            userBridgeService.confirmPasswordReset(userPasswordRequestModel);
        } catch (CommunicationFailureException e) {
            throw new Exception(e);
        }
        return "redirect:/login";
    }

    private void setTemporalAuthenticationForPasswordReset(String token) {
        Authentication authentication = new JWTTokenAuthentication.Builder()
                .withDetails(jwtTokenPayloadReader.readPayload(token))
                .withToken(token)
                .build();
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
