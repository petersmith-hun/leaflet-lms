package hu.psprog.leaflet.lms.service.facade;

import hu.psprog.leaflet.api.rest.request.user.UpdateProfileRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserCreateRequestModel;
import hu.psprog.leaflet.api.rest.request.user.UserPasswordRequestModel;
import hu.psprog.leaflet.api.rest.response.user.ExtendedUserDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.role.AvailableRole;

import java.util.List;

/**
 * User operations facade.
 *
 * @author Peter Smith
 */
public interface UserFacade {

    /**
     * Returns list of existing users.
     *
     * @return list of existing users
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    List<ExtendedUserDataModel> listUsers() throws CommunicationFailureException;

    /**
     * Returns enum of available roles.
     *
     * @return enum of available roles
     */
    AvailableRole[] getAvailableRoles();

    /**
     * Processes user creation request.
     *
     * @param userCreateRequestModel user data
     * @return ID of the created user
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    Long processUserCreation(UserCreateRequestModel userCreateRequestModel) throws CommunicationFailureException;

    /**
     * Returns details of given user by their ID.
     *
     * @param userID ID of the user to retrieve details for
     * @return user data as {@link ExtendedUserDataModel}
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    ExtendedUserDataModel retrieveUserDetails(Long userID) throws CommunicationFailureException;

    /**
     * Changes given user's role to the specified one.
     *
     * @param userID ID of the user to change role for
     * @param newRole new user role
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    void processUserRoleChange(Long userID, AvailableRole newRole) throws CommunicationFailureException;

    /**
     * Processes user profile update request.
     *
     * @param userID ID of the user to update profile for
     * @param updateProfileRequestModel updated user data
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    void processUserProfileUpdate(Long userID, UpdateProfileRequestModel updateProfileRequestModel) throws CommunicationFailureException;

    /**
     * Processes password change request.
     *
     * @param userID ID of the user to change password for
     * @param userPasswordRequestModel new password with its confirmation
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    void processPasswordChange(Long userID, UserPasswordRequestModel userPasswordRequestModel) throws CommunicationFailureException;

    /**
     * Processes account deletion request.
     *
     * @param userID ID of the user to delete account of
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    void processAccountDeletion(Long userID) throws CommunicationFailureException;
}
