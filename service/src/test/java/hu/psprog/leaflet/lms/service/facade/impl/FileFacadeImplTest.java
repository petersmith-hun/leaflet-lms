package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.file.FilesByFolder;
import hu.psprog.leaflet.lms.service.facade.impl.utility.FileBrowser;
import hu.psprog.leaflet.lms.service.facade.impl.utility.URLUtilities;
import hu.psprog.leaflet.lsrs.api.request.DirectoryCreationRequestModel;
import hu.psprog.leaflet.lsrs.api.request.FileUploadRequestModel;
import hu.psprog.leaflet.lsrs.api.request.UpdateFileMetaInfoRequestModel;
import hu.psprog.leaflet.lsrs.api.response.DirectoryDataModel;
import hu.psprog.leaflet.lsrs.api.response.DirectoryListDataModel;
import hu.psprog.leaflet.lsrs.api.response.FileDataModel;
import hu.psprog.leaflet.lsrs.api.response.FileListDataModel;
import hu.psprog.leaflet.lsrs.client.FileBridgeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Unit tests for {@link FileFacadeImpl}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
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

    @Test
    public void shouldGetUploadedFiles() throws CommunicationFailureException {

        // given
        given(fileBridgeService.getUploadedFiles()).willReturn(FileListDataModel.builder()
                .files(List.of(prepareFileDataModel()))
                .build());

        // when
        List<FileDataModel> result = fileFacade.getUploadedFiles();

        // then
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).reference(), equalTo(REFERENCE));
    }

    @Test
    public void shouldReturnEmptyListOnEmptyResponse() throws CommunicationFailureException {

        // given
        given(fileBridgeService.getUploadedFiles()).willReturn(FileListDataModel.builder().build());

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
        assertThat(result.files(), notNullValue());
        assertThat(result.subFolders(), notNullValue());
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
        assertThat(result.reference(), equalTo(REFERENCE));
    }

    @ParameterizedTest
    @MethodSource("acceptableMimeTypesDataProvider")
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
        UUID result = fileFacade.processFileUpload(FileUploadRequestModel.builder().build());

        // then
        assertThat(result, equalTo(PATH_UUID));
    }

    @Test
    public void shouldProcessFileUploadReturnNullOnFailure() throws CommunicationFailureException {

        // given
        given(fileBridgeService.uploadFile(any(FileUploadRequestModel.class))).willReturn(null);

        // when
        UUID result = fileFacade.processFileUpload(FileUploadRequestModel.builder().build());

        // then
        assertThat(result, nullValue());
        verifyNoInteractions(urlUtilities);
    }

    @Test
    public void shouldProcessUpdateFileMetaInfo() throws CommunicationFailureException {

        // given
        UpdateFileMetaInfoRequestModel updateFileMetaInfoRequestModel = UpdateFileMetaInfoRequestModel.builder().build();

        // when
        fileFacade.processUpdateFileMetaInfo(PATH_UUID, updateFileMetaInfoRequestModel);

        // then
        verify(fileBridgeService).updateFileMetaInfo(PATH_UUID, updateFileMetaInfoRequestModel);
    }

    @Test
    public void shouldProcessCreateDirectory() throws CommunicationFailureException {

        // given
        DirectoryCreationRequestModel directoryCreationRequestModel = DirectoryCreationRequestModel.builder().build();

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
        return DirectoryListDataModel.builder()
                .acceptors(List.of(
                        prepareDirectoryDataModel(IMAGES_ROOT, FAKE_MIME_IMAGE_JPG, FAKE_MIME_IMAGE_PNG),
                        prepareDirectoryDataModel(OTHERS_ROOT, FAKE_MIME_OTHER_EXE, FAKE_MIME_OTHER_JAR)
                ))
                .build();
    }

    private DirectoryDataModel prepareDirectoryDataModel(String root, String... mime) {
        return DirectoryDataModel.builder()
                .root(root)
                .acceptableMimeTypes(Arrays.asList(mime))
                .build();
    }

    private FileDataModel prepareFileDataModel() {
        return FileDataModel.builder()
                .reference(REFERENCE)
                .build();
    }

    private static String prepareFakeMimeType(String rootType, String subType) {
        return rootType + "/" + subType;
    }

    private static Stream<Arguments> acceptableMimeTypesDataProvider() {
        
        return Stream.of(
                Arguments.of(IMAGES_ROOT, Arrays.asList(FAKE_MIME_IMAGE_JPG, FAKE_MIME_IMAGE_PNG)),
                Arguments.of(OTHERS_ROOT, Arrays.asList(FAKE_MIME_OTHER_EXE, FAKE_MIME_OTHER_JAR)),
                Arguments.of(IMAGES_ROOT + "/sub1", Arrays.asList(FAKE_MIME_IMAGE_JPG, FAKE_MIME_IMAGE_PNG)),
                Arguments.of(OTHERS_ROOT + "/sub2/sub3", Arrays.asList(FAKE_MIME_OTHER_EXE, FAKE_MIME_OTHER_JAR))
        );
    }
}