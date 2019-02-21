package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.user.PasswordResetDemandRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UpdateProfileRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UpdateRoleRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserCreateRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserPasswordRequestModel;
import hu.psprog.leaflet.api.rest.response.user.ExtendedUserDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.UserBridgeService;
import hu.psprog.leaflet.jwt.auth.support.service.AuthenticationService;
import hu.psprog.leaflet.lms.service.domain.role.AvailableRole;
import hu.psprog.leaflet.lms.service.facade.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link UserFacade}.
 *
 * @author Peter Smith
 */
@Service
public class UserFacadeImpl implements UserFacade {

    private UserBridgeService userBridgeService;
    private AuthenticationService authenticationService;

    @Autowired
    public UserFacadeImpl(UserBridgeService userBridgeService, AuthenticationService authenticationService) {
        this.userBridgeService = userBridgeService;
        this.authenticationService = authenticationService;
    }

    @Override
    public void demandPasswordReset(PasswordResetDemandRequestModel passwordResetDemandRequestModel) throws CommunicationFailureException {
        authenticationService.demandPasswordReset(passwordResetDemandRequestModel);
    }

    @Override
    public void confirmPasswordReset(UserPasswordRequestModel userPasswordRequestModel, String token) throws CommunicationFailureException {
        authenticationService.confirmPasswordReset(userPasswordRequestModel, token);
    }

    @Override
    public void renewToken(Authentication authentication) throws CommunicationFailureException {
        authenticationService.renewToken(authentication);
    }

    @Override
    public void revokeToken() throws CommunicationFailureException {
        authenticationService.revokeToken();
    }

    @Override
    public Authentication claimToken(Authentication authentication) throws CommunicationFailureException {
        return authenticationService.claimToken(authentication);
    }

    @Override
    public List<ExtendedUserDataModel> listUsers() throws CommunicationFailureException {
        return Optional.ofNullable(userBridgeService.getAllUsers().getUsers())
                .orElse(Collections.emptyList());
    }

    @Override
    public AvailableRole[] getAvailableRoles() {
        return AvailableRole.values();
    }

    @Override
    public Long processUserCreation(UserCreateRequestModel userCreateRequestModel) throws CommunicationFailureException {
        return Optional.ofNullable(userBridgeService.createUser(userCreateRequestModel))
                .map(ExtendedUserDataModel::getId)
                .orElse(0L);
    }

    @Override
    public ExtendedUserDataModel retrieveUserDetails(Long userID) throws CommunicationFailureException {
        return userBridgeService.getUserByID(userID);
    }

    @Override
    public void processUserRoleChange(Long userID, AvailableRole newRole) throws CommunicationFailureException {
        userBridgeService.updateRole(userID, mapAvailableRoleToUpdateRoleRequestModel(newRole));
    }

    @Override
    public void processUserProfileUpdate(Long userID, UpdateProfileRequestModel updateProfileRequestModel) throws CommunicationFailureException {
        userBridgeService.updateProfile(userID, updateProfileRequestModel);
    }

    @Override
    public void processPasswordChange(Long userID, UserPasswordRequestModel userPasswordRequestModel) throws CommunicationFailureException {
        userBridgeService.updatePassword(userID, userPasswordRequestModel);
    }

    @Override
    public void processAccountDeletion(Long userID, String password) throws CommunicationFailureException {

        SecurityContextHolder.getContext().setAuthentication(reAuthenticate(userID, password));
        userBridgeService.deleteUser(userID);
    }

    private Authentication reAuthenticate(Long userID, String password) throws CommunicationFailureException {

        ExtendedUserDataModel user = userBridgeService.getUserByID(userID);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), password);
        Authentication reAuthenticatedUser = claimToken(usernamePasswordAuthenticationToken);
        revokeToken();

        return reAuthenticatedUser;
    }

    private UpdateRoleRequestModel mapAvailableRoleToUpdateRoleRequestModel(AvailableRole role) {

        UpdateRoleRequestModel updateRoleRequestModel = new UpdateRoleRequestModel();
        updateRoleRequestModel.setRole(role.name());

        return updateRoleRequestModel;
    }
}
