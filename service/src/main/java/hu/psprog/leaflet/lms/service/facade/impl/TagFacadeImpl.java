package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.tag.TagAssignmentRequestModel;
import hu.psprog.leaflet.api.rest.request.tag.TagCreateRequestModel;
import hu.psprog.leaflet.api.rest.response.entry.EditEntryDataModel;
import hu.psprog.leaflet.api.rest.response.tag.TagDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.TagBridgeService;
import hu.psprog.leaflet.lms.service.domain.entry.ModifyEntryRequest;
import hu.psprog.leaflet.lms.service.facade.TagFacade;
import hu.psprog.leaflet.lms.service.util.EntityConnectionDifferenceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link TagFacade}.
 *
 * @author Peter Smith
 */
@Service
public class TagFacadeImpl implements TagFacade {

    private final EntityConnectionDifferenceCalculator entityConnectionDifferenceCalculator;
    private final TagBridgeService tagBridgeService;

    @Autowired
    public TagFacadeImpl(EntityConnectionDifferenceCalculator entityConnectionDifferenceCalculator, TagBridgeService tagBridgeService) {
        this.entityConnectionDifferenceCalculator = entityConnectionDifferenceCalculator;
        this.tagBridgeService = tagBridgeService;
    }

    @Override
    public List<TagDataModel> getAllTags() throws CommunicationFailureException {
        return Optional.ofNullable(tagBridgeService.getAllTags().tags())
                .orElse(Collections.emptyList());
    }

    @Override
    public void handleAssignmentsOnChange(ModifyEntryRequest modifyEntryRequest, EditEntryDataModel editEntryDataModel) throws CommunicationFailureException {
        EntityConnectionDifferenceCalculator.EntityConnectionContext<Long, TagDataModel> connectionContext =
                entityConnectionDifferenceCalculator.createContextFor(modifyEntryRequest.getTags(), editEntryDataModel.tags(), TagDataModel::id);

        for (Long tagID : connectionContext.collectForAttach()) {
            tagBridgeService.attachTag(createTagAssignmentRequestModel(editEntryDataModel, tagID));
        }

        for (Long tagID : connectionContext.collectForDetach()) {
            tagBridgeService.detachTag(createTagAssignmentRequestModel(editEntryDataModel, tagID));
        }
    }

    @Override
    public TagDataModel getTag(Long tagID) throws CommunicationFailureException {
        return tagBridgeService.getTag(tagID);
    }

    @Override
    public Long processCreateTag(TagCreateRequestModel tagCreateRequestModel) throws CommunicationFailureException {
        return Optional.ofNullable(tagBridgeService.createTag(tagCreateRequestModel))
                .map(TagDataModel::id)
                .orElse(null);
    }

    @Override
    public void processEditTag(Long tagID, TagCreateRequestModel tagCreateRequestModel) throws CommunicationFailureException {
        tagBridgeService.updateTag(tagID, tagCreateRequestModel);
    }

    @Override
    public boolean processStatusChange(Long tagID) throws CommunicationFailureException {
        return tagBridgeService.changeStatus(tagID).enabled();
    }

    @Override
    public void processDeleteTag(Long tagID) throws CommunicationFailureException {
        tagBridgeService.deleteTag(tagID);
    }

    private TagAssignmentRequestModel createTagAssignmentRequestModel(EditEntryDataModel entryDataModel, Long tagID) {

        TagAssignmentRequestModel tagAssignmentRequestModel = new TagAssignmentRequestModel();
        tagAssignmentRequestModel.setEntryID(entryDataModel.id());
        tagAssignmentRequestModel.setTagID(tagID);

        return tagAssignmentRequestModel;
    }
}
