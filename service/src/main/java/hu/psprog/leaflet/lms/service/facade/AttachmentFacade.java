package hu.psprog.leaflet.lms.service.facade;

import hu.psprog.leaflet.api.rest.response.entry.EditEntryDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.entry.ModifyEntryRequest;

/**
 * Attachment operations facade.
 *
 * @author Peter Smith
 */
public interface AttachmentFacade {

    /**
     * Handles assignments between attachments and entries on change operations (create/edit).
     * Assigning/un-assigning attachments are done accordingly the difference between the currently stored and the updated status.
     *
     * @param modifyEntryRequest {@link ModifyEntryRequest} object holding updated status
     * @param editEntryDataModel {@link EditEntryDataModel} holding current status
     * @throws CommunicationFailureException if Bridge cannot reach Leaflet
     */
    void handleAssignmentsOnChange(ModifyEntryRequest modifyEntryRequest, EditEntryDataModel editEntryDataModel) throws CommunicationFailureException;
}
