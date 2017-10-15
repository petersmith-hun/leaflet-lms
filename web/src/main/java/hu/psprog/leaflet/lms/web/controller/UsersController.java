package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.user.UserCreateRequestModel;
import hu.psprog.leaflet.api.rest.response.user.ExtendedUserDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.role.AvailableRole;
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
 * User management controller for LMS.
 *
 * @author Peter Smith
 */
@Controller
@RequestMapping(UsersController.PATH_USERS)
public class UsersController extends BaseController {

    private static final String VIEW_USERS_LIST = "view/users/list";
    private static final String VIEW_USERS_CREATE = "view/users/create";
    private static final String VIEW_USERS_DETAILS = "view/users/details";
    private static final String VIEW_USERS_CHANGE_ROLE = "view/users/change_role";

    private static final String USER_SUCCESSFULLY_CREATED = "User successfully created.";
    private static final String USER_ROLE_SUCCESSFULLY_UPDATED_TO = "User role successfully updated to %s";

    private static final String MODEL_ATTRIBUTE_ROLE = "role";
    private static final String PATH_CHANGE_ROLE = PATH_EDIT + "/role";

    static final String PATH_USERS = "/users";

    private UserFacade userFacade;

    @Autowired
    public UsersController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    /**
     * Returns list of existing users.
     *
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView listUsers() throws CommunicationFailureException {

        return modelAndViewFactory.createForView(VIEW_USERS_LIST)
                .withAttribute("users", userFacade.listUsers())
                .build();
    }

    /**
     * Shows user creation form.
     *
     * @return populated {@link ModelAndView} object
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_CREATE)
    public ModelAndView showUserCreateForm() {

        return modelAndViewFactory.createForView(VIEW_USERS_CREATE)
                .withAttribute("roles", userFacade.getAvailableRoles())
                .build();
    }

    /**
     * Processes user creation request.
     * Redirects to view details page upon success.
     *
     * @param userCreateRequestModel user data
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object, redirection to view user details page
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_CREATE)
    public ModelAndView processUserCreation(@ModelAttribute UserCreateRequestModel userCreateRequestModel, RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        Long createdID = userFacade.processUserCreation(userCreateRequestModel);
        String viewPath = replaceIDInViewPath(createdID);

        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, USER_SUCCESSFULLY_CREATED);

        return modelAndViewFactory.createRedirectionTo(viewPath);
    }

    /**
     * Shows user details page.
     *
     * @param userID ID of the user to view details of
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_VIEW)
    public ModelAndView viewUser(@PathVariable(PATH_VARIABLE_ID) Long userID) throws CommunicationFailureException {

        return modelAndViewFactory.createForView(VIEW_USERS_DETAILS)
                .withAttribute("user", userFacade.retrieveUserDetails(userID))
                .build();
    }

    /**
     * Shows user role change form.
     *
     * @param userID ID of the user to change role for
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_CHANGE_ROLE)
    public ModelAndView showChangeUserRoleForm(@PathVariable(PATH_VARIABLE_ID) Long userID) throws CommunicationFailureException {

        ExtendedUserDataModel details = userFacade.retrieveUserDetails(userID);

        return modelAndViewFactory.createForView(VIEW_USERS_CHANGE_ROLE)
                .withAttribute("roles", userFacade.getAvailableRoles())
                .withAttribute("currentRole", details.getRole())
                .withAttribute("username", details.getUsername())
                .build();
    }

    /**
     * Processes user role change request.
     *
     * @param userID ID of the user to change role for
     * @param role new role of the user
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object, redirection to view user details page
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_CHANGE_ROLE)
    public ModelAndView processChangingUserRole(@PathVariable(PATH_VARIABLE_ID) Long userID, @ModelAttribute(MODEL_ATTRIBUTE_ROLE) AvailableRole role,
                                                RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        userFacade.processUserRoleChange(userID, role);
        String viewPath = PATH_USERS + replaceIDInViewPath(userID);

        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, String.format(USER_ROLE_SUCCESSFULLY_UPDATED_TO, role.name()));

        return modelAndViewFactory.createRedirectionTo(viewPath);
    }
}
