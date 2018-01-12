package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.document.DocumentCreateRequestModel;
import hu.psprog.leaflet.api.rest.request.document.DocumentUpdateRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.facade.DocumentFacade;
import hu.psprog.leaflet.lms.web.auth.mock.WithMockedJWTUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link DocumentsController}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WithMockedJWTUser(userID = 5L)
public class DocumentsControllerTest extends AbstractControllerTest {

    private static final String DOCUMENTS = "documents";
    private static final long DOCUMENT_ID = 2L;
    private static final String DOCUMENT_VIEW_PATH = "/documents/view/" + DOCUMENT_ID;

    @Mock
    private DocumentFacade documentFacade;

    @InjectMocks
    private DocumentsController documentsController;

    @Test
    public void shouldListDocuments() throws CommunicationFailureException {

        // when
        documentsController.listDocuments();

        // then
        verify(documentFacade).getAllDocuments();
        verifyViewCreated("list");
        verifyFieldsSet(DOCUMENTS);
    }

    @Test
    public void shouldViewDocument() throws CommunicationFailureException {

        // when
        documentsController.viewDocument(DOCUMENT_ID);

        // then
        verify(documentFacade).getDocument(DOCUMENT_ID);
        verifyViewCreated("details");
        verifyFieldsSet("document");
    }

    @Test
    public void shouldShowCreateDocumentForm() {

        // when
        documentsController.showCreateDocumentForm();

        // then
        verifyViewCreated("edit_form");
    }

    @Test
    public void shouldProcessCreateDocument() throws CommunicationFailureException {

        // given
        DocumentCreateRequestModel documentCreateRequestModel = new DocumentCreateRequestModel();
        given(documentFacade.processCreateDocument(documentCreateRequestModel)).willReturn(DOCUMENT_ID);

        // when
        documentsController.processCreateDocument(documentCreateRequestModel, redirectAttributes);

        // then
        verify(documentFacade).processCreateDocument(documentCreateRequestModel);
        assertThat(documentCreateRequestModel.getUserID(), equalTo(USER_ID));
        verifyFlashMessageSet();
        verifyRedirectionCreated(DOCUMENT_VIEW_PATH);
    }

    @Test
    public void shouldShowEditDocumentForm() throws CommunicationFailureException {

        // when
        documentsController.showEditDocumentForm(DOCUMENT_ID);

        // then
        verify(documentFacade).getDocument(DOCUMENT_ID);
        verifyViewCreated("edit_form");
        verifyFieldsSet("document");
    }

    @Test
    public void shouldProcessEditDocument() throws CommunicationFailureException {

        // given
        DocumentUpdateRequestModel documentUpdateRequestModel = new DocumentUpdateRequestModel();

        // when
        documentsController.processEditDocument(DOCUMENT_ID, documentUpdateRequestModel, redirectAttributes);

        // then
        verify(documentFacade).processEditDocument(DOCUMENT_ID, documentUpdateRequestModel);
        verifyFlashMessageSet();
        verifyRedirectionCreated(DOCUMENT_VIEW_PATH);
    }

    @Test
    public void shouldProcessDeleteDocument() throws CommunicationFailureException {

        // when
        documentsController.processDeleteDocument(DOCUMENT_ID, redirectAttributes);

        // then
        verify(documentFacade).processDeleteDocument(DOCUMENT_ID);
        verifyFlashMessageSet();
        verifyRedirectionCreated("/documents");
    }
    @Test
    public void shouldProcessChangeStatusWithEnabledStatusMessage() throws CommunicationFailureException {

        // given
        given(documentFacade.processChangeDocumentStatus(DOCUMENT_ID)).willReturn(true);

        // when
        documentsController.processChangeDocumentStatus(DOCUMENT_ID, redirectAttributes);

        // then
        verify(documentFacade).processChangeDocumentStatus(DOCUMENT_ID);
        verifyStatusFlashMessage(true);
        verifyRedirectionCreated(DOCUMENT_VIEW_PATH);
    }

    @Test
    public void shouldProcessChangeStatusWithDisabledStatusMessage() throws CommunicationFailureException {

        // given
        given(documentFacade.processChangeDocumentStatus(DOCUMENT_ID)).willReturn(false);

        // when
        documentsController.processChangeDocumentStatus(DOCUMENT_ID, redirectAttributes);

        // then
        verify(documentFacade).processChangeDocumentStatus(DOCUMENT_ID);
        verifyStatusFlashMessage(false);
        verifyRedirectionCreated(DOCUMENT_VIEW_PATH);
    }

    @Override
    String controllerViewGroup() {
        return DOCUMENTS;
    }
}