package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.document.DocumentCreateRequestModel;
import hu.psprog.leaflet.api.rest.request.document.DocumentUpdateRequestModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.document.EditDocumentDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.DocumentBridgeService;
import hu.psprog.leaflet.lms.service.facade.DocumentFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link DocumentFacade}.
 *
 * @author Peter Smith
 */
@Service
public class DocumentFacadeImpl implements DocumentFacade {

    private DocumentBridgeService documentBridgeService;

    @Autowired
    public DocumentFacadeImpl(DocumentBridgeService documentBridgeService) {
        this.documentBridgeService = documentBridgeService;
    }

    @Override
    public List<EditDocumentDataModel> getAllDocuments() throws CommunicationFailureException {
        return Optional.ofNullable(documentBridgeService.getAllDocuments().getDocuments())
                .orElse(Collections.emptyList());
    }

    @Override
    public WrapperBodyDataModel<EditDocumentDataModel> getDocument(Long documentID) throws CommunicationFailureException {
        return documentBridgeService.getDocumentByID(documentID);
    }

    @Override
    public Long processCreateDocument(DocumentCreateRequestModel documentCreateRequestModel) throws CommunicationFailureException {
        return Optional.ofNullable(documentBridgeService.createDocument(documentCreateRequestModel))
                .map(EditDocumentDataModel::getId)
                .orElse(null);
    }

    @Override
    public void processEditDocument(Long documentID, DocumentUpdateRequestModel documentUpdateRequestModel) throws CommunicationFailureException {
        documentBridgeService.updateDocument(documentID, documentUpdateRequestModel);
    }

    @Override
    public void processDeleteDocument(Long documentID) throws CommunicationFailureException {
        documentBridgeService.deleteDocument(documentID);
    }

    @Override
    public boolean processChangeDocumentStatus(Long documentID) throws CommunicationFailureException {
        return documentBridgeService.changeStatus(documentID).isEnabled();
    }
}
