package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.user.PasswordChangeRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UpdateProfileRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import hu.psprog.leaflet.lms.service.facade.UserFacade;
import hu.psprog.leaflet.lms.web.auth.mock.WithMockedJWTUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletException;

import static org.mockito.Mockito.doThrow;
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

    private static final String FIELD_USER = "user";

    private static final String PATH_ROOT = "/";
    private static final String PATH_LOGIN = "/login";

    private static final String VIEW_PROFILE = "profile";
    private static final String VIEW_PASSWORD = "password";
    private static final String VIEW_DELETE = "delete";
    private static final String PATH_ACCOUNT_UPDATE_PROFILE = "/account/update-profile";
    private static final String PATH_ACCOUNT_CHANGE_PASSWORD = "/account/change-password";

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
        verifyViewCreated(VIEW_PROFILE);
        verifyFieldsSet(FIELD_USER);
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
        verifyRedirectionCreated(PATH_ROOT);
    }

    @Test
    public void shouldProcessUpdateProfileHandleValidationFailure() throws CommunicationFailureException {

        // given
        UpdateProfileRequestModel updateProfileRequestModel = new UpdateProfileRequestModel();
        doThrow(new ValidationFailureException(response)).when(userFacade).processUserProfileUpdate(USER_ID, updateProfileRequestModel);

        // when
        accountController.processUpdateProfile(updateProfileRequestModel, redirectAttributes);

        // then
        verifyValidationViolationInfoSet(updateProfileRequestModel);
        verifyRedirectionCreated(PATH_ACCOUNT_UPDATE_PROFILE);
    }

    @Test
    public void shouldShowPasswordChangeForm() {

        // when
        accountController.showPasswordChangeForm();

        // then
        verifyViewCreated(VIEW_PASSWORD);
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
        verifyRedirectionCreated(PATH_LOGIN);
    }

    @Test
    public void shouldProcessPasswordChangeHandleValidationFailure() throws CommunicationFailureException {

        // given
        PasswordChangeRequestModel passwordChangeRequestModel = new PasswordChangeRequestModel();
        doThrow(new ValidationFailureException(response)).when(userFacade).processPasswordChange(USER_ID, passwordChangeRequestModel);

        // when
        accountController.processPasswordChange(passwordChangeRequestModel, redirectAttributes, request);

        // then
        verifyValidationViolationInfoSet(passwordChangeRequestModel);
        verifyRedirectionCreated(PATH_ACCOUNT_CHANGE_PASSWORD);
    }

    @Test
    public void shouldShowDeleteAccountForm() {

        // when
        accountController.showDeleteAccountForm();

        // then
        verifyViewCreated(VIEW_DELETE);
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
        verifyRedirectionCreated(PATH_LOGIN);
    }

    @Override
    String controllerViewGroup() {
        return USERS;
    }
}