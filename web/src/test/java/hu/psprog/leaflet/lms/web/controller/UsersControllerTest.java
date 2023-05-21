package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.user.UserCreateRequestModel;
import hu.psprog.leaflet.api.rest.response.user.ExtendedUserDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import hu.psprog.leaflet.lms.service.domain.role.AvailableRole;
import hu.psprog.leaflet.lms.service.facade.UserFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link UsersController}.
 *
 * @author Peter Smith
 */
@Extensions({
        @ExtendWith(MockitoExtension.class),
        @ExtendWith(SpringExtension.class)
})
public class UsersControllerTest extends AbstractControllerTest {

    private static final String USERS = "users";

    private static final String FIELD_ROLES = "roles";
    private static final String FIELD_USER = "user";
    private static final String FIELD_CURRENT_ROLE = "currentRole";
    private static final String FIELD_USERNAME = "username";

    private static final String VIEW_CREATE = "create";
    private static final String VIEW_CHANGE_ROLE = "change_role";
    private static final String USERS_VIEW_PATH = "/users/view/" + USER_ID;
    private static final String PATH_USERS_CREATE = "/users/create";

    @Mock
    private UserFacade userFacade;

    private UsersController usersController;

    @BeforeEach
    public void setup() {
        super.setup();
        usersController = new UsersController(modelAndViewFactory, userFacade);
    }

    @Test
    public void shouldListUsers() throws CommunicationFailureException {

        // when
        usersController.listUsers();

        // then
        verify(userFacade).listUsers();
        verifyViewCreated(VIEW_LIST);
        verifyFieldsSet(USERS);
    }

    @Test
    public void shouldShowUserCreateForm() {

        // when
        usersController.showUserCreateForm();

        // then
        verify(userFacade).getAvailableRoles();
        verifyViewCreated(VIEW_CREATE);
        verifyFieldsSet(FIELD_ROLES);
    }

    @Test
    public void shouldProcessUserCreation() throws CommunicationFailureException {

        // given
        UserCreateRequestModel userCreateRequestModel = new UserCreateRequestModel();
        given(userFacade.processUserCreation(userCreateRequestModel)).willReturn(USER_ID);

        // when
        usersController.processUserCreation(userCreateRequestModel, redirectAttributes);

        // then
        verify(userFacade).processUserCreation(userCreateRequestModel);
        verifyFlashMessageSet();
        verifyRedirectionCreated(USERS_VIEW_PATH);
    }

    @Test
    public void shouldProcessUserCreationHandleValidationFailure() throws CommunicationFailureException {

        // given
        UserCreateRequestModel userCreateRequestModel = new UserCreateRequestModel();
        doThrow(new ValidationFailureException(response)).when(userFacade).processUserCreation(userCreateRequestModel);

        // when
        usersController.processUserCreation(userCreateRequestModel, redirectAttributes);

        // then
        verifyValidationViolationInfoSet(userCreateRequestModel);
        verifyRedirectionCreated(PATH_USERS_CREATE);
    }

    @Test
    public void shouldViewUser() throws CommunicationFailureException {

        // when
        usersController.viewUser(USER_ID);

        // then
        verify(userFacade).retrieveUserDetails(USER_ID);
        verifyViewCreated(VIEW_DETAILS);
        verifyFieldsSet(FIELD_USER);
    }

    @Test
    public void shouldShowChangeUserRoleForm() throws CommunicationFailureException {

        // given
        given(userFacade.retrieveUserDetails(USER_ID)).willReturn(ExtendedUserDataModel.getBuilder().build());

        // when
        usersController.showChangeUserRoleForm(USER_ID);

        // then
        verify(userFacade).retrieveUserDetails(USER_ID);
        verify(userFacade).getAvailableRoles();
        verifyViewCreated(VIEW_CHANGE_ROLE);
        verifyFieldsSet(FIELD_ROLES, FIELD_CURRENT_ROLE, FIELD_USERNAME);
    }

    @Test
    public void shouldProcessChangingUserRole() throws CommunicationFailureException {

        // given
        AvailableRole newRole = AvailableRole.ADMIN;

        // when
        usersController.processChangingUserRole(USER_ID, newRole, redirectAttributes);

        // then
        verify(userFacade).processUserRoleChange(USER_ID, newRole);
        verifyFlashMessageSet();
        verifyRedirectionCreated(USERS_VIEW_PATH);
    }

    @Override
    String controllerViewGroup() {
        return USERS;
    }
}