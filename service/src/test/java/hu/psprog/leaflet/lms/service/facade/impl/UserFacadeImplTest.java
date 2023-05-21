package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.user.UpdateProfileRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UpdateRoleRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserCreateRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserPasswordRequestModel;
import hu.psprog.leaflet.api.rest.response.user.ExtendedUserDataModel;
import hu.psprog.leaflet.api.rest.response.user.UserListDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.UserBridgeService;
import hu.psprog.leaflet.lms.service.domain.role.AvailableRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link UserFacadeImpl}.
 *
 * @author Peter Smith
 */
@Extensions({
        @ExtendWith(MockitoExtension.class),
        @ExtendWith(SpringExtension.class)
})
@TestExecutionListeners(listeners = {
        DirtiesContextTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class})
public class UserFacadeImplTest {

    private static final String USERNAME = "test-user";
    private static final String EMAIL = "test-user@dev.local";
    private static final String CREDENTIALS = "credentials";
    private static final Long USER_ID = 1L;

    @Mock
    private UserBridgeService userBridgeService;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private Authentication authentication;

    @InjectMocks
    private UserFacadeImpl userFacade;

    @BeforeEach
    public void setup() {
        given(authentication.getPrincipal()).willReturn(USERNAME);
        given(authentication.getCredentials()).willReturn(CREDENTIALS);
    }

    @Test
    public void shouldListUsers() throws CommunicationFailureException {

        // given
        given(userBridgeService.getAllUsers()).willReturn(prepareUserListDataModel());

        // when
        List<ExtendedUserDataModel> result = userFacade.listUsers();

        // then
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).username(), equalTo(USERNAME));
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
    public void shouldProcessAccountDeletion() throws CommunicationFailureException {

        // when
        userFacade.processAccountDeletion(USER_ID);

        // then
        verify(userBridgeService).deleteUser(USER_ID);
    }

    private UpdateRoleRequestModel prepareUpdateToAdminRoleRequestModel() {

        UpdateRoleRequestModel updateRoleRequestModel = new UpdateRoleRequestModel();
        updateRoleRequestModel.setRole("ADMIN");

        return updateRoleRequestModel;
    }

    private UserListDataModel prepareUserListDataModel() {
        return UserListDataModel.getBuilder()
                .withUsers(List.of(prepareExtendedUserDataModel()))
                .build();
    }

    private ExtendedUserDataModel prepareExtendedUserDataModel() {
        return ExtendedUserDataModel.getBuilder()
                .withId(USER_ID)
                .withUsername(USERNAME)
                .withEmail(EMAIL)
                .build();
    }
}