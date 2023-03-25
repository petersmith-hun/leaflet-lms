package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.entry.EntryInitialStatus;
import hu.psprog.leaflet.api.rest.request.entry.EntrySearchParameters;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import hu.psprog.leaflet.lms.service.domain.entry.EntryFormContent;
import hu.psprog.leaflet.lms.service.domain.entry.ModifyEntryRequest;
import hu.psprog.leaflet.lms.service.facade.CategoryFacade;
import hu.psprog.leaflet.lms.service.facade.EntryFacade;
import hu.psprog.leaflet.lms.web.auth.mock.WithMockedJWTUser;
import hu.psprog.leaflet.lms.web.controller.pagination.EntryPaginationHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link EntriesController}.
 *
 * @author Peter Smith
 */
@Extensions({
        @ExtendWith(MockitoExtension.class),
        @ExtendWith(SpringExtension.class)
})
@WithMockedJWTUser(userID = 5L)
public class EntriesControllerTest extends AbstractControllerTest {

    private static final String ENTRIES = "entries";
    private static final long ENTRY_ID = 4L;
    private static final String ENTRY_VIEW_PATH = "/entries/view/" + ENTRY_ID;
    private static final String FIELD_TAG_SELECTOR = "tagSelector";
    private static final String FIELD_CATEGORY_SELECTOR = "categorySelector";
    private static final String FIELD_FILE_SELECTOR = "fileSelector";
    private static final String FIELD_ENTRY_DATA = "entryData";
    private static final String FIELD_RESOURCE_SERVER_URL = "resourceServerUrl";
    private static final String FIELD_CATEGORIES = "categories";
    private static final String PATH_ENTRIES = "/entries";
    private static final String PATH_ENTRIES_CREATE = PATH_ENTRIES + "/create";
    private static final EntrySearchParameters ENTRY_SEARCH_PARAMETERS = new EntrySearchParameters();
    private static final WrapperBodyDataModel WRAPPER_BODY_DATA_MODEL = WrapperBodyDataModel.getBuilder().build();

    @Mock
    private EntryFacade entryFacade;

    @Mock
    private CategoryFacade categoryFacade;

    @Mock
    private EntryPaginationHelper entryPaginationHelper;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private EntriesController entriesController;

    @Test
    public void shouldListEntries() throws CommunicationFailureException {

        // given
        given(entryFacade.getEntries(ENTRY_SEARCH_PARAMETERS)).willReturn(WRAPPER_BODY_DATA_MODEL);

        // when
        entriesController.listEntries(ENTRY_SEARCH_PARAMETERS, request);

        // then
        verify(entryFacade).getEntries(ENTRY_SEARCH_PARAMETERS);
        verify(categoryFacade).getAllCategories();
        verify(entryPaginationHelper).extractPaginationAttributes(WRAPPER_BODY_DATA_MODEL, request);
        verifyViewCreated(VIEW_LIST);
        verifyFieldsSet(FIELD_CONTENT, FIELD_CATEGORIES, FIELD_PAGINATION);
    }

    @Test
    public void shouldViewEntry() throws CommunicationFailureException {

        // given
        given(entryFacade.getEntry(ENTRY_ID)).willReturn(WRAPPER_BODY_DATA_MODEL);

        // when
        entriesController.viewEntry(ENTRY_ID);

        // then
        verify(entryFacade).getEntry(ENTRY_ID);
        verifyViewCreated(VIEW_DETAILS);
        verifyFieldsSet(FIELD_ENTRY_DATA, FIELD_RESOURCE_SERVER_URL);
    }

    @Test
    public void shouldShowCreateForm() throws CommunicationFailureException {

        // given
        given(entryFacade.fillForm()).willReturn(EntryFormContent.getBuilder().build());

        // when
        entriesController.showCreateEntryForm();

        // then
        verify(entryFacade).fillForm();
        verifyViewCreated(VIEW_EDIT_FORM);
        verifyFieldsSet(FIELD_TAG_SELECTOR, FIELD_CATEGORY_SELECTOR, FIELD_FILE_SELECTOR, FIELD_RESOURCE_SERVER_URL);
    }

    @Test
    public void shouldProcessEntryCreation() throws CommunicationFailureException {

        // given
        ModifyEntryRequest modifyEntryRequest = new ModifyEntryRequest();
        given(entryFacade.processCreateEntry(modifyEntryRequest)).willReturn(ENTRY_ID);

        // when
        entriesController.processEntryCreation(modifyEntryRequest, redirectAttributes);

        // then
        verify(entryFacade).processCreateEntry(modifyEntryRequest);
        assertThat(modifyEntryRequest.getUserID(), equalTo(USER_ID));
        verifyFlashMessageSet();
        verifyRedirectionCreated(ENTRY_VIEW_PATH);
    }

    @Test
    public void shouldProcessEntryCreationHandleValidationFailure() throws CommunicationFailureException {

        // given
        ModifyEntryRequest modifyEntryRequest = new ModifyEntryRequest();
        doThrow(new ValidationFailureException(response)).when(entryFacade).processCreateEntry(modifyEntryRequest);

        // when
        entriesController.processEntryCreation(modifyEntryRequest, redirectAttributes);

        // then
        verifyValidationViolationInfoSet(modifyEntryRequest);
        verifyRedirectionCreated(PATH_ENTRIES_CREATE);
    }

    @Test
    public void shouldShowEditEntryForm() throws CommunicationFailureException {

        // given
        given(entryFacade.fillForm(ENTRY_ID)).willReturn(EntryFormContent.getBuilder().build());

        // when
        entriesController.showEditEntryForm(ENTRY_ID);

        // then
        verify(entryFacade).fillForm(ENTRY_ID);
        verifyViewCreated(VIEW_EDIT_FORM);
        verifyFieldsSet(FIELD_TAG_SELECTOR, FIELD_CATEGORY_SELECTOR, FIELD_FILE_SELECTOR, FIELD_ENTRY_DATA, FIELD_RESOURCE_SERVER_URL);
    }

    @Test
    public void shouldProcessEditEntry() throws CommunicationFailureException {

        // given
        ModifyEntryRequest modifyEntryRequest = new ModifyEntryRequest();

        // when
        entriesController.processEntryEditing(ENTRY_ID, modifyEntryRequest, redirectAttributes);

        // then
        verify(entryFacade).processEditEntry(ENTRY_ID, modifyEntryRequest);
        verifyFlashMessageSet();
        verifyRedirectionCreated(ENTRY_VIEW_PATH);
    }

    @Test
    public void shouldProcessEditEntryHandleValidationFailure() throws CommunicationFailureException {

        // given
        ModifyEntryRequest modifyEntryRequest = new ModifyEntryRequest();
        doThrow(new ValidationFailureException(response)).when(entryFacade).processEditEntry(ENTRY_ID, modifyEntryRequest);

        // when
        entriesController.processEntryEditing(ENTRY_ID, modifyEntryRequest, redirectAttributes);

        // then
        verifyValidationViolationInfoSet(modifyEntryRequest);
        verifyRedirectionCreated(ENTRY_VIEW_PATH);
    }

    @Test
    public void shouldProcessChangeStatusWithEnabledStatusMessage() throws CommunicationFailureException {

        // given
        given(entryFacade.processStatusChange(ENTRY_ID)).willReturn(true);

        // when
        entriesController.processStatusChange(ENTRY_ID, redirectAttributes);

        // then
        verify(entryFacade).processStatusChange(ENTRY_ID);
        verifyStatusFlashMessage(true);
        verifyRedirectionCreated(ENTRY_VIEW_PATH);
    }

    @Test
    public void shouldProcessChangeStatusWithDisabledStatusMessage() throws CommunicationFailureException {

        // given
        given(entryFacade.processStatusChange(ENTRY_ID)).willReturn(false);

        // when
        entriesController.processStatusChange(ENTRY_ID, redirectAttributes);

        // then
        verify(entryFacade).processStatusChange(ENTRY_ID);
        verifyStatusFlashMessage(false);
        verifyRedirectionCreated(ENTRY_VIEW_PATH);
    }

    @ParameterizedTest
    @EnumSource(EntryInitialStatus.class)
    public void shouldProcessPublicationTransition(EntryInitialStatus publicationStatus) throws CommunicationFailureException {

        // when
        entriesController.processPublicationTransition(ENTRY_ID, publicationStatus, redirectAttributes);

        // then
        verify(entryFacade).processPublicationStatusTransition(ENTRY_ID, publicationStatus);
        verifyFlashMessageSet();
        verifyRedirectionCreated(ENTRY_VIEW_PATH);
    }

    @Test
    public void shouldProcessDeletion() throws CommunicationFailureException {

        // when
        entriesController.processDeletion(ENTRY_ID, redirectAttributes);

        // then
        verify(entryFacade).processDeletion(ENTRY_ID);
        verifyFlashMessageSet();
        verifyRedirectionCreated(PATH_ENTRIES);
    }

    @Override
    String controllerViewGroup() {
        return ENTRIES;
    }
}