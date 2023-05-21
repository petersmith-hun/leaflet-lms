package hu.psprog.leaflet.lms.service.facade.impl.utility;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lsrs.api.response.DirectoryDataModel;
import hu.psprog.leaflet.lsrs.api.response.DirectoryListDataModel;
import hu.psprog.leaflet.lsrs.api.response.FileDataModel;
import hu.psprog.leaflet.lsrs.api.response.FileListDataModel;
import hu.psprog.leaflet.lsrs.client.FileBridgeService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link FileBrowser}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class FileBrowserTest {

    private static final String REFERENCE_PATTERN = "/%s/image.jpg";
    private static final String IMAGES_PART1_IMAGE1_JPG = "images/part1/image1.jpg";
    private static final String IMAGES_IMAGE2_JPG = "images/image2.jpg";
    private static final String IMAGES_IMAGE3_JPG = "images/image3.jpg";
    private static final String IMAGES_PART1_IMAGE4_JPG = "images/part1/image4.jpg";
    private static final String IMAGES_PART1_IMAGE5_JPG = "images/part1/image5.jpg";
    private static final String IMAGES_PART1_IMAGE6_JPG = "images/part1/image6.jpg";
    private static final String IMAGES_PART2_IMAGE7_JPG = "images/part2/image7.jpg";
    private static final String ROOT_IMAGES = "images";
    private static final String ROOT_OTHERS = "others";
    private static final String SUB_1 = "sub1";
    private static final String SUB_2 = "sub2";
    private static final String SUB_3 = "sub3";
    private static final String SUB_4 = "sub4";
    private static final String SUB_5 = "sub5";
    private static final String SUB1_SUB3 = "sub1/sub3";
    private static final String SUB1_SUB4 = "sub1/sub4";
    private static final String SUB2_SUB5= "sub2/sub5";
    private static final String SUB2_SUB5_SUB6 = "sub2/sub5/sub6";

    @Mock(strictness = Mock.Strictness.LENIENT)
    private FileBridgeService fileBridgeService;

    @Spy
    private URLUtilities urlUtilities;

    @InjectMocks
    private FileBrowser fileBrowser;

    @ParameterizedTest
    @MethodSource("filesDataProvider")
    public void shouldGetFilesUnderPath(String path, List<String> expectedFiles) throws CommunicationFailureException {

        // given
        given(fileBridgeService.getUploadedFiles()).willReturn(prepareFileListDataModel());

        // when
        List<FileDataModel> result = fileBrowser.getFiles(path);

        // then
        assertThat(result.size(), equalTo(expectedFiles.size()));
        assertThat(result.stream().map(FileDataModel::path).toList().containsAll(expectedFiles), is(true));
    }

    @ParameterizedTest
    @MethodSource("foldersDataProvider")
    public void shouldGetFoldersUnderPath(String path, List<String> expectedFolders) throws CommunicationFailureException {

        // given
        given(fileBridgeService.getDirectories()).willReturn(prepareDirectoryListDataModel());

        // when
        List<String> result = fileBrowser.getFolders(path);

        // then
        assertThat(result.size(), equalTo(expectedFolders.size()));
        assertThat(result.containsAll(expectedFolders), is(true));
    }

    private DirectoryListDataModel prepareDirectoryListDataModel() {
        return DirectoryListDataModel.builder()
                .acceptors(List.of(
                        prepareDirectoryDataModel(ROOT_IMAGES),
                        prepareDirectoryDataModel(ROOT_OTHERS)
                ))
                .build();
    }

    private DirectoryDataModel prepareDirectoryDataModel(String root) {
        return DirectoryDataModel.builder()
                .root(root)
                .children(Arrays.asList(SUB_1, SUB_2, SUB1_SUB3, SUB1_SUB4, SUB2_SUB5, SUB2_SUB5_SUB6))
                .build();
    }

    private FileListDataModel prepareFileListDataModel() {
        return FileListDataModel.builder()
                .files(List.of(
                        prepareFileDataModel(IMAGES_PART1_IMAGE1_JPG),
                        prepareFileDataModel(IMAGES_IMAGE2_JPG),
                        prepareFileDataModel(IMAGES_IMAGE3_JPG),
                        prepareFileDataModel(IMAGES_PART1_IMAGE4_JPG),
                        prepareFileDataModel(IMAGES_PART1_IMAGE5_JPG),
                        prepareFileDataModel(IMAGES_PART1_IMAGE6_JPG),
                        prepareFileDataModel(IMAGES_PART2_IMAGE7_JPG)
                ))
                .build();
    }

    private FileDataModel prepareFileDataModel(String path) {
        return FileDataModel.builder()
                .reference(String.format(REFERENCE_PATTERN, UUID.randomUUID()))
                .path(path)
                .build();
    }

    private static Stream<Arguments> filesDataProvider() {
        
        return Stream.of(
                Arguments.of("images", Arrays.asList(IMAGES_IMAGE2_JPG, IMAGES_IMAGE3_JPG)),
                Arguments.of("images/part1", Arrays.asList(IMAGES_PART1_IMAGE1_JPG, IMAGES_PART1_IMAGE4_JPG, IMAGES_PART1_IMAGE5_JPG, IMAGES_PART1_IMAGE6_JPG)),
                Arguments.of("images/part2", Collections.singletonList(IMAGES_PART2_IMAGE7_JPG)),
                Arguments.of("/images/", Arrays.asList(IMAGES_IMAGE2_JPG, IMAGES_IMAGE3_JPG)),
                Arguments.of(StringUtils.EMPTY, Collections.emptyList()),
                Arguments.of("/non/existing/path", Collections.emptyList()),
                Arguments.of(null, Collections.emptyList())
        );
    }

    private static Stream<Arguments> foldersDataProvider() {
        
        return Stream.of(
                Arguments.of(null, Arrays.asList(ROOT_IMAGES, ROOT_OTHERS)),
                Arguments.of(StringUtils.EMPTY, Arrays.asList(ROOT_IMAGES, ROOT_OTHERS)),
                Arguments.of(ROOT_IMAGES, Arrays.asList(SUB_1, SUB_2)),
                Arguments.of("/" + ROOT_IMAGES + "/", Arrays.asList(SUB_1, SUB_2)),
                Arguments.of(ROOT_IMAGES + "/" + SUB_1, Arrays.asList(SUB_3, SUB_4)),
                Arguments.of(ROOT_OTHERS + "/" + SUB_2, Collections.singletonList(SUB_5)),
                Arguments.of("/" + ROOT_OTHERS + "/" + SUB_2 + "/", Collections.singletonList(SUB_5)),
                Arguments.of(ROOT_OTHERS + "/" + SUB2_SUB5_SUB6, Collections.emptyList()),
                Arguments.of("non-existing-root", Collections.emptyList()),
                Arguments.of("non-existing-root/non-existing-sub", Collections.emptyList())
        );
    }
}