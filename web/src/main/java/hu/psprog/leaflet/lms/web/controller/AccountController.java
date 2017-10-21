package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.user.UpdateProfileRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserPasswordRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.facade.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import static hu.psprog.leaflet.lms.web.config.SecurityConfiguration.PATH_LOGIN;

/**
 * Account management (authenticated user self operations) controller for LMS.
 *
 * @author Peter Smith
 */
@Controller
@RequestMapping(AccountController.PATH_ACCOUNT)
public class AccountController extends BaseController {

    private static final String VIEW_USERS_PROFILE = "view/users/profile";
    private static final String VIEW_USERS_PASSWORD = "view/users/password";
    private static final String VIEW_USERS_DELETE = "view/users/delete";

    private static final String PATH_UPDATE_PROFILE = "/update-profile";
    private static final String PATH_CHANGE_PASSWORD = "/change-password";
    private static final String PATH_DELETE_ACCOUNT = "/delete-account";

    private static final String YOUR_ACCOUNT_HAS_SUCCESSFULLY_BEEN_UPDATED = "Your account has successfully been updated.";
    private static final String YOUR_PASSWORD_HAS_BEEN_CHANGED_PLEASE_RE_LOGIN = "Your password has been changed. Please re-login.";
    private static final String YOUR_ACCOUNT_HAS_BEEN_DELETED = "Your account has been deleted.";

    private static final String MODEL_ATTRIBUTE_PASSWORD = "password";

    static final String PATH_ACCOUNT = "/account";

    private UserFacade userFacade;

    @Autowired
    public AccountController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    /**
     * Shows user profile update form.
     *
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_UPDATE_PROFILE)
    public ModelAndView showUpdateProfileForm() throws CommunicationFailureException {

        return modelAndViewFactory.createForView(VIEW_USERS_PROFILE)
                .withAttribute("user", userFacade.retrieveUserDetails(currentUserID()))
                .build();
    }

    /**
     * Processes user profile update request.
     *
     * @param updateProfileRequestModel updated profile information
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object, redirection to LMS home
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_UPDATE_PROFILE)
    public ModelAndView processUpdateProfile(@ModelAttribute UpdateProfileRequestModel updateProfileRequestModel, RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        userFacade.processUserProfileUpdate(currentUserID(), updateProfileRequestModel);
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, YOUR_ACCOUNT_HAS_SUCCESSFULLY_BEEN_UPDATED);

        return modelAndViewFactory.createRedirectionTo(PATH_HOME);
    }

    /**
     * Shows password change form.
     *
     * @return populated {@link ModelAndView} object
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_CHANGE_PASSWORD)
    public ModelAndView showPasswordChangeForm() {

        return modelAndViewFactory.createForView(VIEW_USERS_PASSWORD)
                .build();
    }

    /**
     * Processes password change request.
     *
     * @param userPasswordRequestModel updated password and its confirmation wrapped as {@link UserPasswordRequestModel}
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object, redirection to logout
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_CHANGE_PASSWORD)
    public ModelAndView processPasswordChange(@ModelAttribute UserPasswordRequestModel userPasswordRequestModel,
                                              RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest)
            throws CommunicationFailureException, ServletException {

        userFacade.processPasswordChange(currentUserID(), userPasswordRequestModel);
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, YOUR_PASSWORD_HAS_BEEN_CHANGED_PLEASE_RE_LOGIN);
        httpServletRequest.logout();

        return modelAndViewFactory.createRedirectionTo(PATH_LOGIN);
    }

    /**
     * Shows account delete confirmation form.
     *
     * @return populated {@link ModelAndView} object
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_DELETE_ACCOUNT)
    public ModelAndView showDeleteAccountForm() {

        return modelAndViewFactory.createForView(VIEW_USERS_DELETE)
                .build();
    }

    /**
     * Processes account deletion request.
     *
     * @param password user's current password to confirm deletion
     * @return populated {@link ModelAndView} object, redirection to logout
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_DELETE_ACCOUNT)
    public ModelAndView processDeleteAccount(@ModelAttribute(MODEL_ATTRIBUTE_PASSWORD) String password,
                                             RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest)
            throws CommunicationFailureException, ServletException {

        userFacade.processAccountDeletion(currentUserID(), password);
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, YOUR_ACCOUNT_HAS_BEEN_DELETED);
        httpServletRequest.logout();

        return modelAndViewFactory.createRedirectionTo(PATH_LOGIN);
    }
}