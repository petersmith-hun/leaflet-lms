package hu.psprog.leaflet.lms.service.facade;

import hu.psprog.leaflet.api.rest.response.entry.EditEntryDataModel;
import hu.psprog.leaflet.api.rest.response.tag.TagDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.entry.ModifyEntryRequest;

import java.util.List;

/**
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
}
