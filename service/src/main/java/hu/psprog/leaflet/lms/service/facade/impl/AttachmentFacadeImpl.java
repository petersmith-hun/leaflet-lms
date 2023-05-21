package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.attachment.AttachmentRequestModel;
import hu.psprog.leaflet.api.rest.response.entry.EditEntryDataModel;
import hu.psprog.leaflet.api.rest.response.file.FileDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.AttachmentBridgeService;
import hu.psprog.leaflet.lms.service.domain.entry.ModifyEntryRequest;
import hu.psprog.leaflet.lms.service.extraction.CommonExtractor;
import hu.psprog.leaflet.lms.service.facade.AttachmentFacade;
import hu.psprog.leaflet.lms.service.util.EntityConnectionDifferenceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementation of {@link AttachmentFacade}.
 *
 * @author Peter Smith
 */
@Service
public class AttachmentFacadeImpl implements AttachmentFacade {

    private final EntityConnectionDifferenceCalculator entityConnectionDifferenceCalculator;
    private final AttachmentBridgeService attachmentBridgeService;
    private final CommonExtractor commonExtractor;

    @Autowired
    public AttachmentFacadeImpl(EntityConnectionDifferenceCalculator entityConnectionDifferenceCalculator,
                                AttachmentBridgeService attachmentBridgeService, CommonExtractor commonExtractor) {
        this.entityConnectionDifferenceCalculator = entityConnectionDifferenceCalculator;
        this.attachmentBridgeService = attachmentBridgeService;
        this.commonExtractor = commonExtractor;
    }

    @Override
    public void handleAssignmentsOnChange(ModifyEntryRequest modifyEntryRequest, EditEntryDataModel editEntryDataModel) throws CommunicationFailureException {
        EntityConnectionDifferenceCalculator.EntityConnectionContext<UUID, FileDataModel> connectionContext =
                entityConnectionDifferenceCalculator.createContextFor(modifyEntryRequest.getAttachments(), editEntryDataModel.attachments(), commonExtractor::extractPathUUIDFromReference);

        for (UUID pathUUID : connectionContext.collectForAttach()) {
            attachmentBridgeService.attach(createAttachmentRequestModel(editEntryDataModel, pathUUID));
        }

        for (UUID pathUUID : connectionContext.collectForDetach()) {
            attachmentBridgeService.detach(createAttachmentRequestModel(editEntryDataModel, pathUUID));
        }
    }

    private AttachmentRequestModel createAttachmentRequestModel(EditEntryDataModel editEntryDataModel, UUID attachmentPathUUID) {

        AttachmentRequestModel attachmentRequestModel = new AttachmentRequestModel();
        attachmentRequestModel.setEntryID(editEntryDataModel.id());
        attachmentRequestModel.setPathUUID(attachmentPathUUID);

        return attachmentRequestModel;
    }
}
