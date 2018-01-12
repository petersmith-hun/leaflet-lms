package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.user.PasswordChangeRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UpdateProfileRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.facade.UserFacade;
import hu.psprog.leaflet.lms.web.auth.mock.WithMockedJWTUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletException;

import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link AccountController}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WithMockedJWTUser(userID = 5L)
public class AccountControllerTest extends AbstractControllerTest {

    private static final String USERS = "users";

    @Mock
    private UserFacade userFacade;

    @InjectMocks
    private AccountController accountController;

    @Test
    public void shouldShowUpdateProfileForm() throws CommunicationFailureException {

        // when
        accountController.showUpdateProfileForm();

        // then
        verify(userFacade).retrieveUserDetails(USER_ID);
        verifyViewCreated("profile");
        verifyFieldsSet("user");
    }

    @Test
    public void shouldProcessUpdateProfile() throws CommunicationFailureException {

        // given
        UpdateProfileRequestModel updateProfileRequestModel = new UpdateProfileRequestModel();

        // when
        accountController.processUpdateProfile(updateProfileRequestModel, redirectAttributes);

        // then
        verify(userFacade).processUserProfileUpdate(USER_ID, updateProfileRequestModel);
        verifyFlashMessageSet();
        verifyRedirectionCreated("/");
    }

    @Test
    public void shouldShowPasswordChangeForm() {

        // when
        accountController.showPasswordChangeForm();

        // then
        verifyViewCreated("password");
    }

    @Test
    public void shouldProcessPasswordChange() throws ServletException, CommunicationFailureException {

        // given
        PasswordChangeRequestModel passwordChangeRequestModel = new PasswordChangeRequestModel();

        // when
        accountController.processPasswordChange(passwordChangeRequestModel, redirectAttributes, request);

        // then
        verify(userFacade).processPasswordChange(USER_ID, passwordChangeRequestModel);
        verifyUserLoggedOut();
        verifyFlashMessageSet();
        verifyRedirectionCreated("/login");
    }

    @Test
    public void shouldShowDeleteAccountForm() {

        // when
        accountController.showDeleteAccountForm();

        // then
        verifyViewCreated("delete");
    }

    @Test
    public void shouldProcessDeleteAccount() throws ServletException, CommunicationFailureException {

        // given
        String password = "test-pw";

        // when
        accountController.processDeleteAccount(password, redirectAttributes, request);

        // then
        verify(userFacade).processAccountDeletion(USER_ID, password);
        verifyFlashMessageSet();
        verifyUserLoggedOut();
        verifyRedirectionCreated("/login");
    }

    @Override
    String controllerViewGroup() {
        return USERS;
    }
}