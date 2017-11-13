package hu.psprog.leaflet.lms.service.facade;

import hu.psprog.leaflet.api.rest.request.tag.TagCreateRequestModel;
import hu.psprog.leaflet.api.rest.response.entry.EditEntryDataModel;
import hu.psprog.leaflet.api.rest.response.tag.TagDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.entry.ModifyEntryRequest;

import java.util.List;

/**
 * Tag operations facade.
 *
 * @author Peter Smith
 */
public interface TagFacade {

    /**
     * Retrieves all existing tags.
     *
     * @return list of tags
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    List<TagDataModel> getAllTags() throws CommunicationFailureException;

    /**
     * Handles assignments between tags and entries on change operations (create/edit).
     * Assigning/un-assigning tags are done accordingly the difference between the currently stored and the updated status.
     *
     * @param modifyEntryRequest {@link ModifyEntryRequest} object holding updated status
     * @param editEntryDataModel {@link EditEntryDataModel} holding current status
     * @throws CommunicationFailureException if Bridge cannot reach Leaflet
     */
    void handleAssignmentsOnChange(ModifyEntryRequest modifyEntryRequest, EditEntryDataModel editEntryDataModel) throws CommunicationFailureException;

    /**
     * Returns details of the tag identified by given ID.
     *
     * @param tagID ID of tag to retrieve details of
     * @return tag data as {@link TagDataModel}
     * @throws CommunicationFailureException if Bridge cannot reach Leaflet
     */
    TagDataModel getTag(Long tagID) throws CommunicationFailureException;

    /**
     * Processes tag creation request.
     *
     * @param tagCreateRequestModel tag data
     * @return ID of the created tag
     * @throws CommunicationFailureException if Bridge cannot reach Leaflet
     */
    Long processCreateTag(TagCreateRequestModel tagCreateRequestModel) throws CommunicationFailureException;

    /**
     * Processes tag update request.
     *
     * @param tagID ID of the tag to update
     * @param tagCreateRequestModel update tag data
     * @throws CommunicationFailureException if Bridge cannot reach Leaflet
     */
    void processEditTag(Long tagID, TagCreateRequestModel tagCreateRequestModel) throws CommunicationFailureException;

    /**
     * Processes tag status change request.
     *
     * @param tagID ID of the tag to update
     * @return current status as boolean, {@code true} if enabled, {@code false} otherwise
     * @throws CommunicationFailureException if Bridge cannot reach Leaflet
     */
    boolean processStatusChange(Long tagID) throws CommunicationFailureException;

    /**
     * Processes tag deletion request.
     *
     * @param tagID ID of the tag to delete
     * @throws CommunicationFailureException if Bridge cannot reach Leaflet
     */
    void processDeleteTag(Long tagID) throws CommunicationFailureException;
}
