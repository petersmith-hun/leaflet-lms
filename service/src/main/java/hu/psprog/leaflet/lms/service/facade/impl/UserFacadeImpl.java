package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.user.UpdateProfileRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UpdateRoleRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserCreateRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserPasswordRequestModel;
import hu.psprog.leaflet.api.rest.response.user.ExtendedUserDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.UserBridgeService;
import hu.psprog.leaflet.lms.service.domain.role.AvailableRole;
import hu.psprog.leaflet.lms.service.facade.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final UserBridgeService userBridgeService;

    @Autowired
    public UserFacadeImpl(UserBridgeService userBridgeService) {
        this.userBridgeService = userBridgeService;
    }

    @Override
    public List<ExtendedUserDataModel> listUsers() throws CommunicationFailureException {
        return Optional.ofNullable(userBridgeService.getAllUsers().users())
                .orElse(Collections.emptyList());
    }

    @Override
    public AvailableRole[] getAvailableRoles() {
        return AvailableRole.values();
    }

    @Override
    public Long processUserCreation(UserCreateRequestModel userCreateRequestModel) throws CommunicationFailureException {
        return Optional.ofNullable(userBridgeService.createUser(userCreateRequestModel))
                .map(ExtendedUserDataModel::id)
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
    public void processAccountDeletion(Long userID) throws CommunicationFailureException {
        userBridgeService.deleteUser(userID);
    }

    private UpdateRoleRequestModel mapAvailableRoleToUpdateRoleRequestModel(AvailableRole role) {

        UpdateRoleRequestModel updateRoleRequestModel = new UpdateRoleRequestModel();
        updateRoleRequestModel.setRole(role.name());

        return updateRoleRequestModel;
    }
}
