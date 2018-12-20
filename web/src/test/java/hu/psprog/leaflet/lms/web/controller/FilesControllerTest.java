package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.file.DirectoryCreationRequestModel;
import hu.psprog.leaflet.api.rest.request.file.FileUploadRequestModel;
import hu.psprog.leaflet.api.rest.request.file.UpdateFileMetaInfoRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import hu.psprog.leaflet.lms.service.facade.FileFacade;
import hu.psprog.leaflet.lms.service.facade.impl.utility.URLUtilities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link FilesController}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class FilesControllerTest extends AbstractControllerTest {

    private static final String FILES = "files";
    private static final UUID PATH_UUID = UUID.randomUUID();
    private static final String FILE_VIEW_PATH = "/files/view/" + PATH_UUID;
    private static final String FILENAME = "image.jpg";
    private static final byte[] FILE_CONTENT = "content".getBytes();

    private static final String FIELD_CURRENT_URL = "currentURL";
    private static final String FIELD_UP_URL = "upURL";
    private static final String FIELD_FILE = "file";
    private static final String FIELD_ACCEPTABLE_MIME_TYPES = "acceptableMimeTypes";

    private static final String VIEW_BROWSER = "browser";
    private static final String VIEW_UPLOAD_FORM = "upload_form";
    private static final String VIEW_DIR_CREATE_FORM = "dir_create_form";

    private static final String PATH_FILES_BROWSE = "/files/browse";
    private static final String SERVLET_PATH = "/servlet/path";
    private static final String PATTERN_FILE_BROWSER_ROOT_PATH = "/files/browse/**";
    private static final String EXTRACTED_SUB_PATH = "/sub/path";

    @Mock
    private FileFacade fileFacade;

    @Mock
    private URLUtilities urlUtilities;

    @InjectMocks
    private FilesController filesController;

    @Test
    public void shouldListFiles() throws CommunicationFailureException {

        // given
        given(request.getServletPath()).willReturn(SERVLET_PATH);
        given(urlUtilities.extractSubPath(PATTERN_FILE_BROWSER_ROOT_PATH, SERVLET_PATH)).willReturn(EXTRACTED_SUB_PATH);

        // when
        filesController.listFiles(request);

        // then
        verify(fileFacade).getFilesByFolder(anyString());
        verify(urlUtilities).extractSubPath(anyString(), anyString());
        verify(urlUtilities).normalize(anyString());
        verify(urlUtilities).getUpURL(anyString(), anyInt());
        verifyViewCreated(VIEW_BROWSER);
        verifyFieldsSet(VIEW_BROWSER, FIELD_CURRENT_URL, FIELD_UP_URL);
    }

    @Test
    public void shouldViewFileDetails() throws CommunicationFailureException {

        // when
        filesController.viewFileDetails(PATH_UUID);

        // then
        verify(fileFacade).getFileDetails(PATH_UUID);
        verifyViewCreated(VIEW_DETAILS);
        verifyFieldsSet(FIELD_FILE);
    }

    @Test
    public void shouldShowEditFileMetaInfoForm() throws CommunicationFailureException {

        // when
        filesController.showEditFileMetaInfoForm(PATH_UUID);

        // then
        verify(fileFacade).getFileDetails(PATH_UUID);
        verifyViewCreated(VIEW_EDIT_FORM);
        verifyFieldsSet(FIELD_FILE);
    }

    @Test
    public void shouldProcessEditFileMetaInfo() throws CommunicationFailureException {

        // given
        UpdateFileMetaInfoRequestModel updateFileMetaInfoRequestModel = new UpdateFileMetaInfoRequestModel();

        // when
        filesController.processEditFileMetaInfo(PATH_UUID, updateFileMetaInfoRequestModel, redirectAttributes);

        // then
        verify(fileFacade).processUpdateFileMetaInfo(PATH_UUID, updateFileMetaInfoRequestModel);
        verifyFlashMessageSet();
        verifyRedirectionCreated(FILE_VIEW_PATH);
    }

    @Test
    public void shouldProcessEditFileMetaInfoHandleValidationFailure() throws CommunicationFailureException {

        // given
        UpdateFileMetaInfoRequestModel updateFileMetaInfoRequestModel = new UpdateFileMetaInfoRequestModel();
        doThrow(new ValidationFailureException(response)).when(fileFacade).processUpdateFileMetaInfo(PATH_UUID, updateFileMetaInfoRequestModel);

        // when
        filesController.processEditFileMetaInfo(PATH_UUID, updateFileMetaInfoRequestModel, redirectAttributes);

        // then
        verifyValidationViolationInfoSet(updateFileMetaInfoRequestModel);
        verifyRedirectionCreated(FILE_VIEW_PATH);
    }

    @Test
    public void shouldShowFileUploadForm() throws CommunicationFailureException {

        // given
        given(request.getServletPath()).willReturn(SERVLET_PATH);
        given(urlUtilities.extractSubPath(PATTERN_FILE_BROWSER_ROOT_PATH, SERVLET_PATH)).willReturn(EXTRACTED_SUB_PATH);
        given(fileFacade.getAcceptableMimeTypes(anyString())).willReturn(Collections.emptyList());

        // when
        filesController.showFileUploadForm(request);

        // then
        verify(fileFacade).getAcceptableMimeTypes(anyString());
        verify(urlUtilities).extractSubPath(anyString(), anyString());
        verifyViewCreated(VIEW_UPLOAD_FORM);
        verifyFieldsSet(FIELD_ACCEPTABLE_MIME_TYPES);
    }

    @Test
    public void shouldProcessFileUpload() throws CommunicationFailureException {

        // given
        FileUploadRequestModel fileUploadRequestModel = new FileUploadRequestModel();
        given(fileFacade.processFileUpload(fileUploadRequestModel)).willReturn(PATH_UUID);

        // when
        filesController.processFileUpload(fileUploadRequestModel, redirectAttributes, request);

        // then
        verify(fileFacade).processFileUpload(fileUploadRequestModel);
        verifyFlashMessageSet();
        verifyRedirectionCreated(FILE_VIEW_PATH);
    }

    @Test
    public void shouldProcessFileUploadHandleValidationFailure() throws CommunicationFailureException {

        // given
        FileUploadRequestModel fileUploadRequestModel = new FileUploadRequestModel();
        doThrow(new ValidationFailureException(response)).when(fileFacade).processFileUpload(fileUploadRequestModel);
        given(request.getServletPath()).willReturn(SERVLET_PATH);

        // when
        filesController.processFileUpload(fileUploadRequestModel, redirectAttributes, request);

        // then
        verifyValidationViolationInfoSet(fileUploadRequestModel);
        verifyRedirectionCreated(SERVLET_PATH);
    }

    @Test
    public void shouldShowCreateDirectoryForm() {

        // when
        filesController.showCreateDirectoryForm();

        // then
        verifyViewCreated(VIEW_DIR_CREATE_FORM);
    }

    @Test
    public void shouldProcessCreateDirectory() throws CommunicationFailureException {

        // given
        DirectoryCreationRequestModel directoryCreationRequestModel = new DirectoryCreationRequestModel();

        // when
        filesController.processCreateDirectory(directoryCreationRequestModel, redirectAttributes, request);

        // then
        verify(fileFacade).processCreateDirectory(directoryCreationRequestModel);
        verifyFlashMessageSet();
        verifyRedirectionCreated(PATH_FILES_BROWSE);
    }

    @Test
    public void shouldProcessCreateDirectoryHandleValidationFailure() throws CommunicationFailureException {

        // given
        DirectoryCreationRequestModel directoryCreationRequestModel = new DirectoryCreationRequestModel();
        doThrow(new ValidationFailureException(response)).when(fileFacade).processCreateDirectory(directoryCreationRequestModel);
        given(request.getServletPath()).willReturn(SERVLET_PATH);

        // when
        filesController.processCreateDirectory(directoryCreationRequestModel, redirectAttributes, request);

        // then
        verifyValidationViolationInfoSet(directoryCreationRequestModel);
        verifyRedirectionCreated(SERVLET_PATH);
    }

    @Test
    public void shouldProcessDeleteFile() throws CommunicationFailureException {

        // when
        filesController.processDeleteFile(PATH_UUID, redirectAttributes);

        // then
        verify(fileFacade).processDeleteFile(PATH_UUID);
        verifyFlashMessageSet();
        verifyRedirectionCreated(PATH_FILES_BROWSE);
    }

    @Test
    public void shouldGetResource() throws CommunicationFailureException {

        // given
        given(fileFacade.getFileForDownload(PATH_UUID, FILENAME)).willReturn(FILE_CONTENT);

        // when
        ResponseEntity<byte[]> result = filesController.getResource(PATH_UUID, FILENAME);

        // then
        verify(fileFacade).getFileForDownload(PATH_UUID, FILENAME);
        assertThat(result.getBody(), equalTo(FILE_CONTENT));
    }

    @Override
    String controllerViewGroup() {
        return FILES;
    }
}