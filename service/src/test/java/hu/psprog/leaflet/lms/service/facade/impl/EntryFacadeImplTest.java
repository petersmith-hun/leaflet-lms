package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.entry.EntryCreateRequestModel;
import hu.psprog.leaflet.api.rest.response.category.CategoryDataModel;
import hu.psprog.leaflet.api.rest.response.common.BaseBodyDataModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EditEntryDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EntryListDataModel;
import hu.psprog.leaflet.api.rest.response.file.FileDataModel;
import hu.psprog.leaflet.api.rest.response.tag.TagDataModel;
import hu.psprog.leaflet.bridge.client.domain.OrderBy;
import hu.psprog.leaflet.bridge.client.domain.OrderDirection;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.EntryBridgeService;
import hu.psprog.leaflet.lms.service.domain.entry.EntryFormContent;
import hu.psprog.leaflet.lms.service.domain.entry.ModifyEntryRequest;
import hu.psprog.leaflet.lms.service.facade.AttachmentFacade;
import hu.psprog.leaflet.lms.service.facade.CategoryFacade;
import hu.psprog.leaflet.lms.service.facade.FileFacade;
import hu.psprog.leaflet.lms.service.facade.TagFacade;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.convert.ConversionService;

import java.util.Collections;

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
@RunWith(JUnitParamsRunner.class)
public class EntryFacadeImplTest {

    private static final long ENTRY_ID = 1L;
    private static final int PAGE = 1;
    private static final int LIMIT = 10;
    private static final OrderBy.Entry ORDER_BY = OrderBy.Entry.CREATED;
    private static final OrderDirection ORDER_DIRECTION = OrderDirection.ASC;
    private static final ModifyEntryRequest MODIFY_ENTRY_REQUEST = new ModifyEntryRequest();
    private static final EntryCreateRequestModel ENTRY_CREATE_REQUEST_MODEL = new EntryCreateRequestModel();

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

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetEntries() throws CommunicationFailureException {

        // given
        WrapperBodyDataModel<EntryListDataModel> response = wrapResponse(prepareEntryListDataModel());
        given(entryBridgeService.getPageOfEntries(PAGE, LIMIT, ORDER_BY, ORDER_DIRECTION)).willReturn(response);

        // when
        WrapperBodyDataModel<EntryListDataModel> result = entryFacade.getEntries(PAGE, LIMIT, ORDER_BY, ORDER_DIRECTION);

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

    @Test
    @Parameters({"true", "false"})
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

        given(categoryFacade.getAllCategories()).willReturn(Collections.singletonList(new CategoryDataModel()));
        given(tagFacade.getAllTags()).willReturn(Collections.singletonList(new TagDataModel()));
        given(fileFacade.getUploadedFiles()).willReturn(Collections.singletonList(new FileDataModel()));
        if (withEntryData) {
            given(entryBridgeService.getEntryByID(ENTRY_ID)).willReturn(wrapResponse(prepareEditEntryDataModel()));
        }
    }

    private void assertFillForm(EntryFormContent result, boolean withEntryData) {

        assertThat(result.getExistingCategories().isEmpty(), is(false));
        assertThat(result.getExistingFiles().isEmpty(), is(false));
        assertThat(result.getExistingTags().isEmpty(), is(false));
        if (withEntryData) {
            assertThat(result.getEntryData(), notNullValue());
        }
    }

    private <T extends BaseBodyDataModel> WrapperBodyDataModel<T> wrapResponse(T response) {
        return WrapperBodyDataModel.getBuilder()
                .withBody(response)
                .build();
    }

    private EntryListDataModel prepareEntryListDataModel() {
        return EntryListDataModel.getBuilder()
                .withItem(prepareEditEntryDataModel())
                .build();
    }

    private EditEntryDataModel prepareEditEntryDataModel() {
        return EditEntryDataModel.getExtendedBuilder()
                .withId(ENTRY_ID)
                .withLink("link-1")
                .withEnabled(true)
                .build();
    }
}