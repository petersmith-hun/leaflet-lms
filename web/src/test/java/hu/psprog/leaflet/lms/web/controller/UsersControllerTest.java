package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.user.UserCreateRequestModel;
import hu.psprog.leaflet.api.rest.response.user.ExtendedUserDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.role.AvailableRole;
import hu.psprog.leaflet.lms.service.facade.UserFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link UsersController}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class UsersControllerTest extends AbstractControllerTest {

    private static final String USERS = "users";

    @Mock
    private UserFacade userFacade;

    @InjectMocks
    private UsersController usersController;

    @Test
    public void shouldListUsers() throws CommunicationFailureException {

        // when
        usersController.listUsers();

        // then
        verify(userFacade).listUsers();
        verifyViewCreated("list");
        verifyFieldsSet(USERS);
    }

    @Test
    public void shouldShowUserCreateForm() {

        // when
        usersController.showUserCreateForm();

        // then
        verify(userFacade).getAvailableRoles();
        verifyViewCreated("create");
        verifyFieldsSet("roles");
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
        verifyRedirectionCreated("/users/view/" + USER_ID);
    }

    @Test
    public void shouldViewUser() throws CommunicationFailureException {

        // when
        usersController.viewUser(USER_ID);

        // then
        verify(userFacade).retrieveUserDetails(USER_ID);
        verifyViewCreated("details");
        verifyFieldsSet("user");
    }

    @Test
    public void shouldShowChangeUserRoleForm() throws CommunicationFailureException {

        // given
        given(userFacade.retrieveUserDetails(USER_ID)).willReturn(ExtendedUserDataModel.getExtendedBuilder().build());

        // when
        usersController.showChangeUserRoleForm(USER_ID);

        // then
        verify(userFacade).retrieveUserDetails(USER_ID);
        verify(userFacade).getAvailableRoles();
        verifyViewCreated("change_role");
        verifyFieldsSet("roles", "currentRole", "username");
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
        verifyRedirectionCreated("/users/view/" + USER_ID);
    }

    @Override
    String controllerViewGroup() {
        return USERS;
    }
}