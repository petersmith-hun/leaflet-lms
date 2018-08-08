package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.document.DocumentCreateRequestModel;
import hu.psprog.leaflet.api.rest.request.document.DocumentUpdateRequestModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.document.DocumentListDataModel;
import hu.psprog.leaflet.api.rest.response.document.EditDocumentDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.DocumentBridgeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link DocumentFacadeImpl}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class DocumentFacadeImplTest {

    private static final long DOCUMENT_ID = 1L;
    private static final String CONTENT = "content";

    @Mock
    private DocumentBridgeService documentBridgeService;

    @InjectMocks
    private DocumentFacadeImpl documentFacade;

    @Test
    public void shouldGetAllDocuments() throws CommunicationFailureException {

        // given
        DocumentListDataModel response = prepareDocumentListDataModel();
        given(documentBridgeService.getAllDocuments()).willReturn(response);

        // when
        List<EditDocumentDataModel> result = documentFacade.getAllDocuments();

        // then
        assertThat(result, equalTo(response.getDocuments()));
    }

    @Test
    public void shouldGetAllDocumentsReturnEmptyListOnEmptyResponse() throws CommunicationFailureException {

        // given
        given(documentBridgeService.getAllDocuments()).willReturn(DocumentListDataModel.getBuilder().build());

        // when
        List<EditDocumentDataModel> result = documentFacade.getAllDocuments();

        // then
        assertThat(result, equalTo(Collections.emptyList()));
    }

    @Test
    public void shouldGetDocument() throws CommunicationFailureException {

        // given
        WrapperBodyDataModel<EditDocumentDataModel> response = WrapperBodyDataModel.getBuilder()
                .withBody(prepareEditDocumentDataModel())
                .build();
        given(documentBridgeService.getDocumentByID(DOCUMENT_ID)).willReturn(response);

        // when
        WrapperBodyDataModel<EditDocumentDataModel> result = documentFacade.getDocument(DOCUMENT_ID);

        // then
        assertThat(result, equalTo(response));
    }

    @Test
    public void shouldProcessCreateDocument() throws CommunicationFailureException {

        // given
        DocumentCreateRequestModel documentCreateRequestModel = prepareDocumentCreateRequestModel();
        given(documentBridgeService.createDocument(documentCreateRequestModel)).willReturn(prepareEditDocumentDataModel());

        // when
        Long result = documentFacade.processCreateDocument(documentCreateRequestModel);

        // then
        assertThat(result, equalTo(DOCUMENT_ID));
        verify(documentBridgeService).createDocument(documentCreateRequestModel);
    }

    @Test
    public void shouldProcessCreateDocumentReturnNullOnFailure() throws CommunicationFailureException {

        // given
        DocumentCreateRequestModel documentCreateRequestModel = prepareDocumentCreateRequestModel();
        given(documentBridgeService.createDocument(documentCreateRequestModel)).willReturn(null);

        // when
        Long result = documentFacade.processCreateDocument(documentCreateRequestModel);

        // then
        assertThat(result, nullValue());
        verify(documentBridgeService).createDocument(documentCreateRequestModel);
    }

    @Test
    public void shouldProcessEditDocument() throws CommunicationFailureException {

        // given
        DocumentUpdateRequestModel documentUpdateRequestModel = prepareDocumentCreateRequestModel();

        // when
        documentFacade.processEditDocument(DOCUMENT_ID, documentUpdateRequestModel);

        // then
        verify(documentBridgeService).updateDocument(DOCUMENT_ID, documentUpdateRequestModel);
    }

    @Test
    public void shouldProcessDeleteDocument() throws CommunicationFailureException {

        // when
        documentFacade.processDeleteDocument(DOCUMENT_ID);

        // then
        verify(documentBridgeService).deleteDocument(DOCUMENT_ID);
    }

    @Test
    public void shouldProcessChangeStatus() throws CommunicationFailureException {

        // given
        given(documentBridgeService.changeStatus(DOCUMENT_ID)).willReturn(prepareEditDocumentDataModel());

        // when
        boolean result = documentFacade.processChangeDocumentStatus(DOCUMENT_ID);

        // then
        assertThat(result, is(true));
    }

    private DocumentCreateRequestModel prepareDocumentCreateRequestModel() {

        DocumentCreateRequestModel documentCreateRequestModel = new DocumentCreateRequestModel();
        documentCreateRequestModel.setRawContent(CONTENT);

        return documentCreateRequestModel;
    }

    private DocumentListDataModel prepareDocumentListDataModel() {
        return DocumentListDataModel.getBuilder()
                .withItem(prepareEditDocumentDataModel())
                .build();
    }

    private EditDocumentDataModel prepareEditDocumentDataModel() {
        return EditDocumentDataModel.getExtendedBuilder()
                .withId(DOCUMENT_ID)
                .withRawContent(CONTENT)
                .withEnabled(true)
                .build();
    }
}