package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.entry.EntryCreateRequestModel;
import hu.psprog.leaflet.api.rest.request.entry.EntryInitialStatus;
import hu.psprog.leaflet.api.rest.request.entry.EntrySearchParameters;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EditEntryDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EntrySearchResultDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.EntryBridgeService;
import hu.psprog.leaflet.lms.service.domain.entry.EntryFormContent;
import hu.psprog.leaflet.lms.service.domain.entry.ModifyEntryRequest;
import hu.psprog.leaflet.lms.service.facade.AttachmentFacade;
import hu.psprog.leaflet.lms.service.facade.CategoryFacade;
import hu.psprog.leaflet.lms.service.facade.EntryFacade;
import hu.psprog.leaflet.lms.service.facade.FileFacade;
import hu.psprog.leaflet.lms.service.facade.TagFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Implementation of {@link EntryFacade}.
 *
 * @author Peter Smith
 */
@Service
public class EntryFacadeImpl implements EntryFacade {

    private final EntryBridgeService entryBridgeService;
    private final TagFacade tagFacade;
    private final FileFacade fileFacade;
    private final CategoryFacade categoryFacade;
    private final AttachmentFacade attachmentFacade;
    private final ConversionService conversionService;

    @Autowired
    public EntryFacadeImpl(EntryBridgeService entryBridgeService, TagFacade tagFacade, FileFacade fileFacade,
                           CategoryFacade categoryFacade, AttachmentFacade attachmentFacade, ConversionService conversionService) {
        this.entryBridgeService = entryBridgeService;
        this.tagFacade = tagFacade;
        this.fileFacade = fileFacade;
        this.categoryFacade = categoryFacade;
        this.attachmentFacade = attachmentFacade;
        this.conversionService = conversionService;
    }

    @Override
    public WrapperBodyDataModel<EntrySearchResultDataModel> getEntries(EntrySearchParameters entrySearchParameters) throws CommunicationFailureException {
        return entryBridgeService.searchEntries(entrySearchParameters);
    }

    @Override
    public WrapperBodyDataModel<EditEntryDataModel> getEntry(Long entryID) throws CommunicationFailureException {
        return entryBridgeService.getEntryByID(entryID);
    }

    @Override
    public EntryFormContent fillForm() throws CommunicationFailureException {
        return fillForm(null);
    }

    @Override
    public EntryFormContent fillForm(Long entryID) throws CommunicationFailureException {

        return EntryFormContent.getBuilder()
                .withExistingCategories(categoryFacade.getAllCategories())
                .withExistingTags(tagFacade.getAllTags())
                .withExistingFiles(fileFacade.getUploadedFiles())
                .withEntryData(Objects.nonNull(entryID)
                        ? entryBridgeService.getEntryByID(entryID)
                        : null)
                .build();
    }

    @Override
    public Long processCreateEntry(ModifyEntryRequest modifyEntryRequest) throws CommunicationFailureException {
        return postHandleResult(modifyEntryRequest, entryBridgeService.createEntry(convert(modifyEntryRequest)));
    }

    @Override
    public void processEditEntry(Long id, ModifyEntryRequest modifyEntryRequest) throws CommunicationFailureException {
        postHandleResult(modifyEntryRequest, entryBridgeService.updateEntry(id, convert(modifyEntryRequest)));
    }

    @Override
    public boolean processStatusChange(Long id) throws CommunicationFailureException {
        return entryBridgeService.changeStatus(id).isEnabled();
    }

    @Override
    public void processPublicationStatusTransition(Long id, EntryInitialStatus newStatus) throws CommunicationFailureException {
        entryBridgeService.changePublicationStatus(id, newStatus);
    }

    @Override
    public void processDeletion(Long id) throws CommunicationFailureException {

        ModifyEntryRequest modifyEntryRequest = new ModifyEntryRequest();
        WrapperBodyDataModel<EditEntryDataModel> editEntryDataModel = entryBridgeService.getEntryByID(id);
        tagFacade.handleAssignmentsOnChange(modifyEntryRequest, editEntryDataModel.getBody());
        attachmentFacade.handleAssignmentsOnChange(modifyEntryRequest, editEntryDataModel.getBody());

        entryBridgeService.deleteEntry(id);
    }

    private EntryCreateRequestModel convert(ModifyEntryRequest modifyEntryRequest) {
        return conversionService.convert(modifyEntryRequest, EntryCreateRequestModel.class);
    }

    private Long postHandleResult(ModifyEntryRequest modifyEntryRequest, EditEntryDataModel editEntryDataModel) throws CommunicationFailureException {

        tagFacade.handleAssignmentsOnChange(modifyEntryRequest, editEntryDataModel);
        attachmentFacade.handleAssignmentsOnChange(modifyEntryRequest, editEntryDataModel);

        return editEntryDataModel.getId();
    }
}
