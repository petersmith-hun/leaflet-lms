package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.user.LoginRequestModel;
import hu.psprog.leaflet.api.rest.request.user.PasswordResetDemandRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UpdateProfileRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UpdateRoleRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserCreateRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserPasswordRequestModel;
import hu.psprog.leaflet.api.rest.response.user.ExtendedUserDataModel;
import hu.psprog.leaflet.api.rest.response.user.LoginResponseDataModel;
import hu.psprog.leaflet.api.rest.response.user.UserListDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.UserBridgeService;
import hu.psprog.leaflet.lms.service.auth.handler.JWTTokenPayloadReader;
import hu.psprog.leaflet.lms.service.auth.util.AbstractTokenRelatedTest;
import hu.psprog.leaflet.lms.service.domain.role.AvailableRole;
import hu.psprog.leaflet.lms.service.exception.TokenAuthenticationFailureException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.text.ParseException;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link UserFacadeImpl}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(listeners = {
        DirtiesContextTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class})
public class UserFacadeImplTest extends AbstractTokenRelatedTest {

    private static final String TOKEN = "auth-token";
    private static final String USERNAME = "test-user";
    private static final String CREDENTIALS = "credentials";
    private static final String EXPECTED_DATE = "2018-01-07 12:12:01+0000";
    private static final Long USER_ID = 1L;

    @Mock
    private UserBridgeService userBridgeService;

    @Mock
    private JWTTokenPayloadReader jwtTokenPayloadReader;

    @Mock
    private AuthenticationUtility authenticationUtility;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserFacadeImpl userFacade;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        given(authentication.getPrincipal()).willReturn(USERNAME);
        given(authentication.getCredentials()).willReturn(CREDENTIALS);
    }

    @Test
    public void shouldDemandPasswordReset() throws CommunicationFailureException {

        // given
        PasswordResetDemandRequestModel passwordResetDemandRequestModel = new PasswordResetDemandRequestModel();

        // when
        userFacade.demandPasswordReset(passwordResetDemandRequestModel);

        // then
        verify(userBridgeService).demandPasswordReset(passwordResetDemandRequestModel);
    }

    @Test
    public void shouldConfirmPasswordReset() throws CommunicationFailureException {

        // given
        UserPasswordRequestModel userPasswordRequestModel = new UserPasswordRequestModel();

        // when
        userFacade.confirmPasswordReset(userPasswordRequestModel, TOKEN);

        // then
        verify(authenticationUtility).createAndStoreTemporal(TOKEN);
        verify(userBridgeService).confirmPasswordReset(userPasswordRequestModel);
    }

    @Test
    public void shouldRenewToken() throws CommunicationFailureException {

        // given
        given(userBridgeService.renewToken()).willReturn(prepareLoginResponseDataModel(true));

        // when
        userFacade.renewToken(authentication);

        // then
        verify(authenticationUtility).replace(USERNAME, TOKEN);
    }

    @Test
    public void shouldRevokeToken() throws CommunicationFailureException {

        // when
        userFacade.revokeToken();

        // then
        verify(userBridgeService).revokeToken();
    }

    @Test
    public void shouldClaimTokenWithSuccess() throws CommunicationFailureException, ParseException {

        // given
        given(userBridgeService.claimToken(prepareLoginRequestModel())).willReturn(prepareLoginResponseDataModel(true));
        given(jwtTokenPayloadReader.readPayload(TOKEN)).willReturn(prepareAuthenticationUserDetailsModel(EXPECTED_DATE));

        // when
        Authentication result = userFacade.claimToken(authentication);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getPrincipal(), equalTo(USERNAME));
        assertThat(result.getAuthorities().contains(SERVICE_ROLE), is(true));
    }

    @Test(expected = TokenAuthenticationFailureException.class)
    public void shouldThrowTokenAuthenticationFailureOnInvalidCredentials() throws CommunicationFailureException {

        // given
        given(userBridgeService.claimToken(prepareLoginRequestModel())).willReturn(prepareLoginResponseDataModel(false));

        // when
        userFacade.claimToken(authentication);

        // then
        // exception expected
    }

    @Test(expected = TokenAuthenticationFailureException.class)
    public void shouldThrowTokenAuthenticationFailureOnNullResponse() throws CommunicationFailureException {

        // given
        given(userBridgeService.claimToken(prepareLoginRequestModel())).willReturn(null);

        // when
        userFacade.claimToken(authentication);

        // then
        // exception expected
    }

    @Test
    public void shouldListUsers() throws CommunicationFailureException {

        // given
        given(userBridgeService.getAllUsers()).willReturn(prepareUserListDataModel());

        // when
        List<ExtendedUserDataModel> result = userFacade.listUsers();

        // then
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getUsername(), equalTo(USERNAME));
    }

    @Test
    public void shouldListUsersReturnEmptyListOnEmptyResponse() throws CommunicationFailureException {

        // given
        given(userBridgeService.getAllUsers()).willReturn(UserListDataModel.getBuilder().build());

        // when
        List<ExtendedUserDataModel> result = userFacade.listUsers();

        // then
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void shouldGetAvailableRoles() {

        // when
        AvailableRole[] result = userFacade.getAvailableRoles();

        // then
        assertThat(result, equalTo(AvailableRole.values()));
    }

    @Test
    public void shouldProcessUserCreation() throws CommunicationFailureException {

        // given
        given(userBridgeService.createUser(any(UserCreateRequestModel.class))).willReturn(prepareExtendedUserDataModel());

        // when
        Long result = userFacade.processUserCreation(new UserCreateRequestModel());

        // then
        assertThat(result, equalTo(USER_ID));
    }

    @Test
    public void shouldProcessUserCreationReturnZeroOnFailure() throws CommunicationFailureException {

        // given
        given(userBridgeService.createUser(any(UserCreateRequestModel.class))).willReturn(null);

        // when
        Long result = userFacade.processUserCreation(new UserCreateRequestModel());

        // then
        assertThat(result, equalTo(0L));
    }

    @Test
    public void shouldRetrieveUserDetails() throws CommunicationFailureException {

        // given
        ExtendedUserDataModel extendedUserDataModel = prepareExtendedUserDataModel();
        given(userBridgeService.getUserByID(USER_ID)).willReturn(extendedUserDataModel);

        // when
        ExtendedUserDataModel result = userFacade.retrieveUserDetails(USER_ID);

        // then
        assertThat(result, equalTo(extendedUserDataModel));
    }

    @Test
    public void shouldProcessUserRoleChange() throws CommunicationFailureException {

        // when
        userFacade.processUserRoleChange(USER_ID, AvailableRole.ADMIN);

        // then
        verify(userBridgeService).updateRole(USER_ID, prepareUpdateToAdminRoleRequestModel());
    }

    @Test
    public void shouldProcessProfileUpdate() throws CommunicationFailureException {

        // given
        UpdateProfileRequestModel updateProfileRequestModel = new UpdateProfileRequestModel();

        // when
        userFacade.processUserProfileUpdate(USER_ID, updateProfileRequestModel);

        // then
        verify(userBridgeService).updateProfile(USER_ID, updateProfileRequestModel);
    }

    @Test
    public void shouldProcessPasswordChange() throws CommunicationFailureException {

        // given
        UserPasswordRequestModel userPasswordRequestModel = new UserPasswordRequestModel();

        // when
        userFacade.processPasswordChange(USER_ID, userPasswordRequestModel);

        // then
        verify(userBridgeService).updatePassword(USER_ID, userPasswordRequestModel);
    }

    @Test
    public void shouldProcessAccountDeletion() throws CommunicationFailureException, ParseException {

        // given
        given(userBridgeService.getUserByID(USER_ID)).willReturn(prepareExtendedUserDataModel());
        given(userBridgeService.claimToken(prepareLoginRequestModel())).willReturn(prepareLoginResponseDataModel(true));
        given(jwtTokenPayloadReader.readPayload(TOKEN)).willReturn(prepareAuthenticationUserDetailsModel(EXPECTED_DATE));

        // when
        userFacade.processAccountDeletion(USER_ID, CREDENTIALS);

        // then
        verify(userBridgeService).revokeToken();
        verify(userBridgeService).deleteUser(USER_ID);
        Authentication result = SecurityContextHolder.getContext().getAuthentication();
        assertThat(result, notNullValue());
        assertThat(result.getPrincipal(), equalTo(USERNAME));
        assertThat(result.getAuthorities().contains(SERVICE_ROLE), is(true));
    }

    private UpdateRoleRequestModel prepareUpdateToAdminRoleRequestModel() {

        UpdateRoleRequestModel updateRoleRequestModel = new UpdateRoleRequestModel();
        updateRoleRequestModel.setRole("ADMIN");

        return updateRoleRequestModel;
    }

    private UserListDataModel prepareUserListDataModel() {
        return UserListDataModel.getBuilder()
                .withItem(prepareExtendedUserDataModel())
                .build();
    }

    private ExtendedUserDataModel prepareExtendedUserDataModel() {
        return ExtendedUserDataModel.getExtendedBuilder()
                .withId(USER_ID)
                .withUsername(USERNAME)
                .withEmail(USERNAME)
                .build();
    }

    private LoginRequestModel prepareLoginRequestModel() {

        LoginRequestModel loginRequestModel = new LoginRequestModel();
        loginRequestModel.setEmail(USERNAME);
        loginRequestModel.setPassword(CREDENTIALS);

        return loginRequestModel;
    }

    private LoginResponseDataModel prepareLoginResponseDataModel(boolean withSuccess) {

        return LoginResponseDataModel.getBuilder()
                .withToken(TOKEN)
                .withStatus(withSuccess
                        ? LoginResponseDataModel.AuthenticationResult.AUTH_SUCCESS
                        : LoginResponseDataModel.AuthenticationResult.INVALID_CREDENTIALS)
                .build();
    }
}