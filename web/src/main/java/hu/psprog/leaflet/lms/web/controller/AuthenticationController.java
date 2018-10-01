package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.user.PasswordResetDemandRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserPasswordRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.facade.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for authentication-related operations.
 *
 * @author Peter Smith
 */
@Controller
public class AuthenticationController extends BaseController {

    private static final String PATH_LOGIN = "/login";
    private static final String PATH_PASSWORD_RESET_DEMAND = "/password-reset";
    private static final String PATH_PASSWORD_RESET_CONFIRM = "/password-reset/{token:.+}";

    private static final String VIEW_USERS_LOGIN = "view/users/login";
    private static final String VIEW_USERS_RECLAIM_DEMAND = "view/users/reclaim_demand";
    private static final String VIEW_USERS_RECLAIM_CONFIRM = "view/users/reclaim_confirm";

    private static final String SUCCESSFUL_PASSWORD_RESET_REQUEST = "Your password reset request was successful. Please follow the steps in the email you will receive soon.";
    private static final String SUCCESSFUL_PASSWORD_RESET = "Your password was successfully reset.";

    private UserFacade userFacade;

    @Autowired
    public AuthenticationController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    /**
     * Renders login form.
     *
     * @return populated {@link ModelAndView} object
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_LOGIN)
    public ModelAndView showLoginForm() {
        return modelAndViewFactory
                .createForView(VIEW_USERS_LOGIN)
                .build();
    }

    /**
     * Renders password reset request form.
     *
     * @return populated {@link ModelAndView} object
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_PASSWORD_RESET_DEMAND)
    public ModelAndView showPasswordResetDemandForm() {
        return modelAndViewFactory
                .createForView(VIEW_USERS_RECLAIM_DEMAND)
                .build();
    }

    /**
     * Renders password reset confirmation form.
     *
     * @return populated {@link ModelAndView} object
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_PASSWORD_RESET_CONFIRM)
    public ModelAndView showPasswordResetConfirmationForm() {
        return modelAndViewFactory
                .createForView(VIEW_USERS_RECLAIM_CONFIRM)
                .build();
    }

    /**
     * Processes password reset request.
     *
     * @param passwordResetDemandRequestModel {@link PasswordResetDemandRequestModel} holding information to request password reset
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object, redirection to self
     * @throws CommunicationFailureException if failed to reach backend
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_PASSWORD_RESET_DEMAND)
    public ModelAndView processPasswordResetDemand(@ModelAttribute PasswordResetDemandRequestModel passwordResetDemandRequestModel,
                                                   RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        return handleValidationFailure(() -> {
            userFacade.demandPasswordReset(passwordResetDemandRequestModel);
            redirectAttributes.addFlashAttribute(FLASH_MESSAGE, SUCCESSFUL_PASSWORD_RESET_REQUEST);

            return modelAndViewFactory.createRedirectionTo(PATH_PASSWORD_RESET_DEMAND);
        }, validationFailureRedirectionSupplier(redirectAttributes, passwordResetDemandRequestModel, PATH_PASSWORD_RESET_DEMAND));
    }

    /**
     * Processes password reset. Sets the new password.
     *
     * @param userPasswordRequestModel {@link UserPasswordRequestModel} holding the new password and its confirmation
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object, redirection to login form
     * @throws CommunicationFailureException if failed to reach backend
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_PASSWORD_RESET_CONFIRM)
    public ModelAndView processPasswordResetConfirmation(@ModelAttribute UserPasswordRequestModel userPasswordRequestModel,
                                                         @PathVariable(PATH_VARIABLE_TOKEN) String token, RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        return handleValidationFailure(() -> {
            userFacade.confirmPasswordReset(userPasswordRequestModel, token);
            redirectAttributes.addFlashAttribute(FLASH_MESSAGE, SUCCESSFUL_PASSWORD_RESET);

            return modelAndViewFactory.createRedirectionTo(PATH_LOGIN);
        }, validationFailureRedirectionSupplier(redirectAttributes, userPasswordRequestModel, PATH_PASSWORD_RESET_DEMAND + "/" + token));
    }
}
