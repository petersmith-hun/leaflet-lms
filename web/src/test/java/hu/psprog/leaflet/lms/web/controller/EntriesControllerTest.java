package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.entry.EntryFormContent;
import hu.psprog.leaflet.lms.service.domain.entry.ModifyEntryRequest;
import hu.psprog.leaflet.lms.service.facade.EntryFacade;
import hu.psprog.leaflet.lms.web.auth.mock.WithMockedJWTUser;
import hu.psprog.leaflet.lms.web.controller.pagination.EntryPaginationHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link EntriesController}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WithMockedJWTUser(userID = 5L)
public class EntriesControllerTest extends AbstractControllerTest {

    private static final String ENTRIES = "entries";
    private static final long ENTRY_ID = 4L;
    private static final String ENTRY_VIEW_PATH = "/entries/view/" + ENTRY_ID;

    @Mock
    private EntryFacade entryFacade;

    @Mock
    private EntryPaginationHelper entryPaginationHelper;

    @InjectMocks
    private EntriesController entriesController;

    @Test
    public void shouldListEntries() throws CommunicationFailureException {

        // given
        given(entryPaginationHelper.extractPage(PAGE)).willReturn(PAGE.get());
        given(entryPaginationHelper.getLimit(LIMIT)).willReturn(LIMIT.get());
        given(entryFacade.getEntries(eq(PAGE.get()), eq(LIMIT.get()), any(), any()))
                .willReturn(WrapperBodyDataModel.getBuilder().build());

        // when
        entriesController.listEntries(PAGE, LIMIT, Optional.empty(), Optional.empty());

        // then
        verify(entryFacade).getEntries(eq(PAGE.get()), eq(LIMIT.get()), any(), any());
        verifyViewCreated("list");
        verifyFieldsSet("content", "pagination");
    }

    @Test
    public void shouldViewEntry() throws CommunicationFailureException {

        // given
        given(entryFacade.getEntry(ENTRY_ID)).willReturn(WrapperBodyDataModel.getBuilder().build());

        // when
        entriesController.viewEntry(ENTRY_ID);

        // then
        verify(entryFacade).getEntry(ENTRY_ID);
        verifyViewCreated("details");
        verifyFieldsSet("content", "seo");
    }

    @Test
    public void shouldShowCreateForm() throws CommunicationFailureException {

        // given
        given(entryFacade.fillForm()).willReturn(EntryFormContent.getBuilder().build());

        // when
        entriesController.showCreateEntryForm();

        // then
        verify(entryFacade).fillForm();
        verifyViewCreated("edit_form");
        verifyFieldsSet("tagSelector", "categorySelector", "fileSelector");
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
    public void shouldShowEditEntryForm() throws CommunicationFailureException {

        // given
        given(entryFacade.fillForm(ENTRY_ID)).willReturn(EntryFormContent.getBuilder().build());

        // when
        entriesController.showEditEntryForm(ENTRY_ID);

        // then
        verify(entryFacade).fillForm(ENTRY_ID);
        verifyViewCreated("edit_form");
        verifyFieldsSet("tagSelector", "categorySelector", "fileSelector", "entryData");
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

    @Test
    public void shouldProcessDeletion() throws CommunicationFailureException {

        // when
        entriesController.processDeletion(ENTRY_ID, redirectAttributes);

        // then
        verify(entryFacade).processDeletion(ENTRY_ID);
        verifyFlashMessageSet();
        verifyRedirectionCreated("/entries");
    }

    @Override
    String controllerViewGroup() {
        return ENTRIES;
    }
}