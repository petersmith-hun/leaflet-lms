package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.user.PasswordResetDemandRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserPasswordRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import hu.psprog.leaflet.lms.service.facade.UserFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static hu.psprog.leaflet.lms.web.auth.mock.MockedJWTUserSecurityContextFactory.TOKEN;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link AuthenticationController}.
 *
 * @author Peter Smith
 */
@Extensions({
        @ExtendWith(MockitoExtension.class),
        @ExtendWith(SpringExtension.class)
})
public class AuthenticationControllerTest extends AbstractControllerTest {

    private static final String USERS = "users";

    private static final String VIEW_LOGIN = "login";
    private static final String VIEW_RECLAIM_DEMAND = "reclaim_demand";
    private static final String VIEW_RECLAIM_CONFIRM = "reclaim_confirm";

    private static final String PATH_PASSWORD_RESET = "/password-reset";
    private static final String PATH_LOGIN = "/login";

    @Mock
    private UserFacade userFacade;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    public void shouldShowLoginForm() {

        // when
        authenticationController.showLoginForm();

        // then
        verifyViewCreated(VIEW_LOGIN);
    }

    @Test
    public void shouldShowPasswordResetDemandForm() {

        // when
        authenticationController.showPasswordResetDemandForm();

        // then
        verifyViewCreated(VIEW_RECLAIM_DEMAND);
    }

    @Test
    public void shouldShowPasswordResetConfirmationForm() {

        // when
        authenticationController.showPasswordResetConfirmationForm();

        // then
        verifyViewCreated(VIEW_RECLAIM_CONFIRM);
    }

    @Test
    public void shouldProcessPasswordResetDemand() throws CommunicationFailureException {

        // given
        PasswordResetDemandRequestModel passwordResetDemandRequestModel = new PasswordResetDemandRequestModel();

        // when
        authenticationController.processPasswordResetDemand(passwordResetDemandRequestModel, redirectAttributes);

        // then
        verify(userFacade).demandPasswordReset(passwordResetDemandRequestModel);
        verifyFlashMessageSet();
        verifyRedirectionCreated(PATH_PASSWORD_RESET);
    }

    @Test
    public void shouldProcessPasswordResetDemandHandleValidationFailure() throws CommunicationFailureException {

        // given
        PasswordResetDemandRequestModel passwordResetDemandRequestModel = new PasswordResetDemandRequestModel();
        doThrow(new ValidationFailureException(response)).when(userFacade).demandPasswordReset(passwordResetDemandRequestModel);

        // when
        authenticationController.processPasswordResetDemand(passwordResetDemandRequestModel, redirectAttributes);

        // then
        verifyValidationViolationInfoSet(passwordResetDemandRequestModel);
        verifyRedirectionCreated(PATH_PASSWORD_RESET);
    }

    @Test
    public void shouldProcessPasswordResetConfirmation() throws CommunicationFailureException {

        // given
        UserPasswordRequestModel userPasswordRequestModel = new UserPasswordRequestModel();

        // when
        authenticationController.processPasswordResetConfirmation(userPasswordRequestModel, TOKEN, redirectAttributes);

        // then
        verify(userFacade).confirmPasswordReset(userPasswordRequestModel, TOKEN);
        verifyFlashMessageSet();
        verifyRedirectionCreated(PATH_LOGIN);
    }

    @Test
    public void shouldProcessPasswordResetConfirmationHandleValidationFailure() throws CommunicationFailureException {

        // given
        UserPasswordRequestModel userPasswordRequestModel = new UserPasswordRequestModel();
        doThrow(new ValidationFailureException(response)).when(userFacade).confirmPasswordReset(userPasswordRequestModel, TOKEN);

        // when
        authenticationController.processPasswordResetConfirmation(userPasswordRequestModel, TOKEN, redirectAttributes);

        // then
        verifyValidationViolationInfoSet(userPasswordRequestModel);
        verifyRedirectionCreated(PATH_PASSWORD_RESET + "/" + TOKEN);
    }

    @Override
    String controllerViewGroup() {
        return USERS;
    }
}