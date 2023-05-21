package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.entry.EntryCreateRequestModel;
import hu.psprog.leaflet.api.rest.request.entry.EntryInitialStatus;
import hu.psprog.leaflet.api.rest.request.entry.EntrySearchParameters;
import hu.psprog.leaflet.api.rest.response.category.CategoryDataModel;
import hu.psprog.leaflet.api.rest.response.common.BaseBodyDataModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EditEntryDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EntrySearchResultDataModel;
import hu.psprog.leaflet.api.rest.response.tag.TagDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.EntryBridgeService;
import hu.psprog.leaflet.lms.service.domain.entry.EntryFormContent;
import hu.psprog.leaflet.lms.service.domain.entry.ModifyEntryRequest;
import hu.psprog.leaflet.lms.service.facade.AttachmentFacade;
import hu.psprog.leaflet.lms.service.facade.CategoryFacade;
import hu.psprog.leaflet.lms.service.facade.FileFacade;
import hu.psprog.leaflet.lms.service.facade.TagFacade;
import hu.psprog.leaflet.lsrs.api.response.FileDataModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link EntryFacadeImpl}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class EntryFacadeImplTest {

    private static final long ENTRY_ID = 1L;
    private static final EntryInitialStatus PUBLICATION_STATUS = EntryInitialStatus.PUBLIC;
    private static final ModifyEntryRequest MODIFY_ENTRY_REQUEST = new ModifyEntryRequest();
    private static final EntryCreateRequestModel ENTRY_CREATE_REQUEST_MODEL = new EntryCreateRequestModel();
    private static final EntrySearchParameters ENTRY_SEARCH_PARAMETERS = new EntrySearchParameters();

    @Mock
    private EntryBridgeService entryBridgeService;

    @Mock
    private TagFacade tagFacade;

    @Mock
    private FileFacade fileFacade;

    @Mock
    private CategoryFacade categoryFacade;

    @Mock
    private AttachmentFacade attachmentFacade;

    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private EntryFacadeImpl entryFacade;

    @Test
    public void shouldGetEntries() throws CommunicationFailureException {

        // given
        WrapperBodyDataModel<EntrySearchResultDataModel> response = wrapResponse(prepareEntrySearchResultDataModel());
        given(entryBridgeService.searchEntries(ENTRY_SEARCH_PARAMETERS)).willReturn(response);

        // when
        WrapperBodyDataModel<EntrySearchResultDataModel> result = entryFacade.getEntries(ENTRY_SEARCH_PARAMETERS);

        // then
        assertThat(result, notNullValue());
        assertThat(result, equalTo(response));
    }

    @Test
    public void shouldGetEntry() throws CommunicationFailureException {

        // given
        WrapperBodyDataModel<EditEntryDataModel> response = wrapResponse(prepareEditEntryDataModel());
        given(entryBridgeService.getEntryByID(ENTRY_ID)).willReturn(response);

        // when
        WrapperBodyDataModel<EditEntryDataModel> result = entryFacade.getEntry(ENTRY_ID);

        // then
        assertThat(result, notNullValue());
        assertThat(result, equalTo(response));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void shouldFillForm(boolean withEntryData) throws CommunicationFailureException {

        // given
        prepareFillFormCall(withEntryData);

        // when
        EntryFormContent result = withEntryData
                ? entryFacade.fillForm(ENTRY_ID)
                : entryFacade.fillForm();

        // then
        assertFillForm(result, withEntryData);
    }

    @Test
    public void shouldProcessCreateEntry() throws CommunicationFailureException {

        // given
        EditEntryDataModel editEntryDataModel = prepareEditEntryDataModel();
        given(conversionService.convert(MODIFY_ENTRY_REQUEST, EntryCreateRequestModel.class)).willReturn(ENTRY_CREATE_REQUEST_MODEL);
        given(entryBridgeService.createEntry(ENTRY_CREATE_REQUEST_MODEL)).willReturn(editEntryDataModel);

        // when
        Long result = entryFacade.processCreateEntry(MODIFY_ENTRY_REQUEST);

        // then
        assertThat(result, equalTo(ENTRY_ID));
        verify(entryBridgeService).createEntry(ENTRY_CREATE_REQUEST_MODEL);
        assertModificationPostSteps(editEntryDataModel);
    }

    @Test
    public void shouldProcessUpdateEntry() throws CommunicationFailureException {

        // given
        EditEntryDataModel editEntryDataModel = prepareEditEntryDataModel();
        given(conversionService.convert(MODIFY_ENTRY_REQUEST, EntryCreateRequestModel.class)).willReturn(ENTRY_CREATE_REQUEST_MODEL);
        given(entryBridgeService.updateEntry(ENTRY_ID, ENTRY_CREATE_REQUEST_MODEL)).willReturn(editEntryDataModel);

        // when
        entryFacade.processEditEntry(ENTRY_ID, MODIFY_ENTRY_REQUEST);

        // then
        verify(entryBridgeService).updateEntry(ENTRY_ID, ENTRY_CREATE_REQUEST_MODEL);
        assertModificationPostSteps(editEntryDataModel);
    }

    @Test
    public void shouldProcessChangeStatus() throws CommunicationFailureException {

        // given
        given(entryBridgeService.changeStatus(ENTRY_ID)).willReturn(prepareEditEntryDataModel());

        // when
        boolean result = entryFacade.processStatusChange(ENTRY_ID);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void shouldProcessPublicationStatusTransition() throws CommunicationFailureException {

        // when
        entryFacade.processPublicationStatusTransition(ENTRY_ID, PUBLICATION_STATUS);

        // then
        verify(entryBridgeService).changePublicationStatus(ENTRY_ID, PUBLICATION_STATUS);
    }

    @Test
    public void shouldProcessDeletion() throws CommunicationFailureException {

        // given
        EditEntryDataModel editEntryDataModel = prepareEditEntryDataModel();
        given(entryBridgeService.getEntryByID(ENTRY_ID)).willReturn(wrapResponse(editEntryDataModel));

        // when
        entryFacade.processDeletion(ENTRY_ID);

        // then
        assertModificationPostSteps(editEntryDataModel);
        verify(entryBridgeService).deleteEntry(ENTRY_ID);
    }

    private void assertModificationPostSteps(EditEntryDataModel editEntryDataModel) throws CommunicationFailureException {
        verify(tagFacade).handleAssignmentsOnChange(MODIFY_ENTRY_REQUEST, editEntryDataModel);
        verify(attachmentFacade).handleAssignmentsOnChange(MODIFY_ENTRY_REQUEST, editEntryDataModel);
    }

    private void prepareFillFormCall(boolean withEntryData) throws CommunicationFailureException {

        given(categoryFacade.getAllCategories()).willReturn(Collections.singletonList(CategoryDataModel.getBuilder().build()));
        given(tagFacade.getAllTags()).willReturn(Collections.singletonList(TagDataModel.getBuilder().build()));
        given(fileFacade.getUploadedFiles()).willReturn(Collections.singletonList(FileDataModel.builder().build()));
        if (withEntryData) {
            given(entryBridgeService.getEntryByID(ENTRY_ID)).willReturn(wrapResponse(prepareEditEntryDataModel()));
        }
    }

    private void assertFillForm(EntryFormContent result, boolean withEntryData) {

        assertThat(result.existingCategories().isEmpty(), is(false));
        assertThat(result.existingFiles().isEmpty(), is(false));
        assertThat(result.existingTags().isEmpty(), is(false));
        if (withEntryData) {
            assertThat(result.entryData(), notNullValue());
            assertThat(result.attachedFileReferences(), equalTo(List.of("ref1")));
        } else {
            assertThat(result.attachedFileReferences(), equalTo(Collections.emptyList()));
        }
    }

    private <T extends BaseBodyDataModel> WrapperBodyDataModel<T> wrapResponse(T response) {
        return WrapperBodyDataModel.<T>getBuilder()
                .withBody(response)
                .build();
    }

    private EntrySearchResultDataModel prepareEntrySearchResultDataModel() {

        return EntrySearchResultDataModel.getBuilder()
                .withEntries(List.of(prepareEditEntryDataModel()))
                .build();
    }

    private EditEntryDataModel prepareEditEntryDataModel() {
        return EditEntryDataModel.getBuilder()
                .withId(ENTRY_ID)
                .withLink("link-1")
                .withEnabled(true)
                .withAttachments(List.of(hu.psprog.leaflet.api.rest.response.file.FileDataModel.getBuilder()
                        .withReference("ref1")
                        .build()))
                .build();
    }
}