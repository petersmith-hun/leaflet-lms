package hu.psprog.leaflet.lms.service.facade;

import hu.psprog.leaflet.api.rest.request.document.DocumentCreateRequestModel;
import hu.psprog.leaflet.api.rest.request.document.DocumentUpdateRequestModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.document.EditDocumentDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;

import java.util.List;

/**
 * Document operations facade.
 *
 * @author Peter Smith
 */
public interface DocumentFacade {

    /**
     * Returns list of existing documents.
     *
     * @return list of existing documents
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    List<EditDocumentDataModel> getAllDocuments() throws CommunicationFailureException;

    /**
     * Returns document identified by given ID.
     *
     * @param documentID ID of the document to retrieve
     * @return existing document as wrapped {@link EditDocumentDataModel} or {@code null}
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    WrapperBodyDataModel<EditDocumentDataModel> getDocument(Long documentID) throws CommunicationFailureException;

    /**
     * Processes document creation request.
     *
     * @param documentCreateRequestModel document data as {@link DocumentCreateRequestModel}
     * @return ID of the created document
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    Long processCreateDocument(DocumentCreateRequestModel documentCreateRequestModel) throws CommunicationFailureException;

    /**
     * Processes document update request.
     *
     * @param documentID ID of the document to update
     * @param documentUpdateRequestModel updated document data as {@link DocumentUpdateRequestModel}
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    void processEditDocument(Long documentID, DocumentUpdateRequestModel documentUpdateRequestModel) throws CommunicationFailureException;

    /**
     * Processes document deletion request.
     *
     * @param documentID ID of the document to delete
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    void processDeleteDocument(Long documentID) throws CommunicationFailureException;

    /**
     * Processes document status change request.
     *
     * @param documentID ID of the document to change status of
     * @return current status as boolean, {@code true} if enabled, {@code false} otherwise
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    boolean processChangeDocumentStatus(Long documentID) throws CommunicationFailureException;
}
