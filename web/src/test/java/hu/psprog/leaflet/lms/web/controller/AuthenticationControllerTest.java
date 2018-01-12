package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.user.PasswordResetDemandRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserPasswordRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.facade.UserFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static hu.psprog.leaflet.lms.web.auth.mock.MockedJWTUserSecurityContextFactory.TOKEN;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link AuthenticationController}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class AuthenticationControllerTest extends AbstractControllerTest {

    private static final String USERS = "users";

    @Mock
    private UserFacade userFacade;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    public void shouldShowLoginForm() {

        // when
        authenticationController.showLoginForm();

        // then
        verifyViewCreated("login");
    }

    @Test
    public void shouldShowPasswordResetDemandForm() {

        // when
        authenticationController.showPasswordResetDemandForm();

        // then
        verifyViewCreated("reclaim_demand");
    }

    @Test
    public void shouldShowPasswordResetConfirmationForm() {

        // when
        authenticationController.showPasswordResetConfirmationForm();

        // then
        verifyViewCreated("reclaim_confirm");
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
        verifyRedirectionCreated("/password-reset");
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
        verifyRedirectionCreated("/login");
    }

    @Override
    String controllerViewGroup() {
        return USERS;
    }
}