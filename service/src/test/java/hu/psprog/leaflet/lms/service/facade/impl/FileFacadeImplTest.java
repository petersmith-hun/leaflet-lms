package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.file.DirectoryCreationRequestModel;
import hu.psprog.leaflet.api.rest.request.file.FileUploadRequestModel;
import hu.psprog.leaflet.api.rest.request.file.UpdateFileMetaInfoRequestModel;
import hu.psprog.leaflet.api.rest.response.file.DirectoryDataModel;
import hu.psprog.leaflet.api.rest.response.file.DirectoryListDataModel;
import hu.psprog.leaflet.api.rest.response.file.FileDataModel;
import hu.psprog.leaflet.api.rest.response.file.FileListDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.FileBridgeService;
import hu.psprog.leaflet.lms.service.domain.file.FilesByFolder;
import hu.psprog.leaflet.lms.service.facade.impl.utility.FileBrowser;
import hu.psprog.leaflet.lms.service.facade.impl.utility.URLUtilities;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Unit tests for {@link FileFacadeImpl}.
 *
 * @author Peter Smith
 */
@RunWith(JUnitParamsRunner.class)
public class FileFacadeImplTest {

    private static final String REFERENCE = "file-ref-1";
    private static final String PATH = "test/path";
    private static final byte[] BYTE_ARRAY = "content".getBytes();
    private static final UUID PATH_UUID = UUID.randomUUID();
    private static final String FILENAME = "image.jpg";

    private static final String IMAGES_ROOT = "image";
    private static final String OTHERS_ROOT = "other";
    private static final String FAKE_MIME_IMAGE_JPG = prepareFakeMimeType(IMAGES_ROOT, "jpg");
    private static final String FAKE_MIME_IMAGE_PNG = prepareFakeMimeType(IMAGES_ROOT, "png");
    private static final String FAKE_MIME_OTHER_EXE = prepareFakeMimeType(OTHERS_ROOT, "exe");
    private static final String FAKE_MIME_OTHER_JAR = prepareFakeMimeType(OTHERS_ROOT, "jar");

    @Mock
    private FileBridgeService fileBridgeService;

    @Mock
    private FileBrowser fileBrowser;

    @Mock
    private URLUtilities urlUtilities;

    @InjectMocks
    private FileFacadeImpl fileFacade;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetUploadedFiles() throws CommunicationFailureException {

        // given
        given(fileBridgeService.getUploadedFiles()).willReturn(FileListDataModel.getBuilder()
                .withItem(prepareFileDataModel())
                .build());

        // when
        List<FileDataModel> result = fileFacade.getUploadedFiles();

        // then
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getReference(), equalTo(REFERENCE));
    }

    @Test
    public void shouldReturnEmptyListOnEmptyResponse() throws CommunicationFailureException {

        // given
        given(fileBridgeService.getUploadedFiles()).willReturn(FileListDataModel.getBuilder().build());

        // when
        List<FileDataModel> result = fileFacade.getUploadedFiles();

        // then
        assertThat(result.isEmpty(), equalTo(true));
    }

    @Test
    public void shouldGetFilesByFolder() throws CommunicationFailureException {

        // given
        given(fileBrowser.getFiles(PATH)).willReturn(Collections.emptyList());
        given(fileBrowser.getFolders(PATH)).willReturn(Collections.emptyList());

        // when
        FilesByFolder result = fileFacade.getFilesByFolder(PATH);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getFiles(), notNullValue());
        assertThat(result.getSubFolders(), notNullValue());
    }

    @Test
    public void shouldGetFileForDownload() throws CommunicationFailureException {

        // given
        given(fileBridgeService.downloadFile(PATH_UUID, FILENAME)).willReturn(new ByteArrayInputStream(BYTE_ARRAY));

        // when
        byte[] result = fileFacade.getFileForDownload(PATH_UUID, FILENAME);

        // then
        assertThat(result, equalTo(BYTE_ARRAY));
    }

    @Test
    public void shouldReturnEmptyByteArrayOnFailure() throws CommunicationFailureException {

        // given
        given(fileBridgeService.downloadFile(PATH_UUID, FILENAME)).willReturn(null);

        // when
        byte[] result = fileFacade.getFileForDownload(PATH_UUID, FILENAME);

        // then
        assertThat(result.length, equalTo(0));
    }

    @Test
    public void shouldGetFileDetails() throws CommunicationFailureException {

        // given
        given(fileBridgeService.getFileDetails(PATH_UUID)).willReturn(prepareFileDataModel());

        // when
        FileDataModel result = fileFacade.getFileDetails(PATH_UUID);

        // then
        assertThat(result.getReference(), equalTo(REFERENCE));
    }

    @Test
    @Parameters(source = AcceptableMimeTypesParameterProvider.class)
    public void shouldGetAcceptanceMimeTypes(String path, List<String> expectedMimeTypes) throws CommunicationFailureException {

        // given
        given(urlUtilities.normalize(path)).willReturn(path);
        given(fileBridgeService.getDirectories()).willReturn(prepareDirectoryListDataModel());

        // when
        List<String> result = fileFacade.getAcceptableMimeTypes(path);

        // then
        assertThat(result.size(), equalTo(expectedMimeTypes.size()));
        assertThat(result.containsAll(expectedMimeTypes), is(true));
    }

    @Test
    public void shouldProcessFileUpload() throws CommunicationFailureException {

        // given
        FileDataModel fileDataModel = prepareFileDataModel();
        given(fileBridgeService.uploadFile(any(FileUploadRequestModel.class))).willReturn(fileDataModel);
        given(urlUtilities.extractFilePathUUID(fileDataModel)).willReturn(PATH_UUID);

        // when
        UUID result = fileFacade.processFileUpload(new FileUploadRequestModel());

        // then
        assertThat(result, equalTo(PATH_UUID));
    }

    @Test
    public void shouldProcessFileUploadReturnNullOnFailure() throws CommunicationFailureException {

        // given
        given(fileBridgeService.uploadFile(any(FileUploadRequestModel.class))).willReturn(null);

        // when
        UUID result = fileFacade.processFileUpload(new FileUploadRequestModel());

        // then
        assertThat(result, nullValue());
        verifyZeroInteractions(urlUtilities);
    }

    @Test
    public void shouldProcessUpdateFileMetaInfo() throws CommunicationFailureException {

        // given
        UpdateFileMetaInfoRequestModel updateFileMetaInfoRequestModel = new UpdateFileMetaInfoRequestModel();

        // when
        fileFacade.processUpdateFileMetaInfo(PATH_UUID, updateFileMetaInfoRequestModel);

        // then
        verify(fileBridgeService).updateFileMetaInfo(PATH_UUID, updateFileMetaInfoRequestModel);
    }

    @Test
    public void shouldProcessCreateDirectory() throws CommunicationFailureException {

        // given
        DirectoryCreationRequestModel directoryCreationRequestModel = new DirectoryCreationRequestModel();

        // when
        fileFacade.processCreateDirectory(directoryCreationRequestModel);

        // then
        verify(fileBridgeService).createDirectory(directoryCreationRequestModel);
    }

    @Test
    public void shouldProcessDeleteFile() throws CommunicationFailureException {

        // when
        fileFacade.processDeleteFile(PATH_UUID);

        // then
        verify(fileBridgeService).deleteFile(PATH_UUID);
    }

    private DirectoryListDataModel prepareDirectoryListDataModel() {
        return DirectoryListDataModel.getBuilder()
                .withItem(prepareDirectoryDataModel(IMAGES_ROOT, FAKE_MIME_IMAGE_JPG, FAKE_MIME_IMAGE_PNG))
                .withItem(prepareDirectoryDataModel(OTHERS_ROOT, FAKE_MIME_OTHER_EXE, FAKE_MIME_OTHER_JAR))
                .build();
    }

    private DirectoryDataModel prepareDirectoryDataModel(String root, String... mime) {
        return DirectoryDataModel.getBuilder()
                .withRoot(root)
                .withAcceptableMimeTypes(Arrays.asList(mime))
                .build();
    }

    private FileDataModel prepareFileDataModel() {
        return FileDataModel.getBuilder()
                .withReference(REFERENCE)
                .build();
    }

    private static String prepareFakeMimeType(String rootType, String subType) {
        return rootType + "/" + subType;
    }

    public static class AcceptableMimeTypesParameterProvider {

        public static Object[] provide() {
            return new Object[] {
                    new Object[] {IMAGES_ROOT, Arrays.asList(FAKE_MIME_IMAGE_JPG, FAKE_MIME_IMAGE_PNG)},
                    new Object[] {OTHERS_ROOT, Arrays.asList(FAKE_MIME_OTHER_EXE, FAKE_MIME_OTHER_JAR)},
                    new Object[] {IMAGES_ROOT + "/sub1", Arrays.asList(FAKE_MIME_IMAGE_JPG, FAKE_MIME_IMAGE_PNG)},
                    new Object[] {OTHERS_ROOT + "/sub2/sub3", Arrays.asList(FAKE_MIME_OTHER_EXE, FAKE_MIME_OTHER_JAR)}
            };
        }
    }
}