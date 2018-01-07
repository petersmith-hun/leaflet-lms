package hu.psprog.leaflet.lms.service.facade.impl.utility;

import hu.psprog.leaflet.api.rest.response.file.DirectoryDataModel;
import hu.psprog.leaflet.api.rest.response.file.DirectoryListDataModel;
import hu.psprog.leaflet.api.rest.response.file.FileDataModel;
import hu.psprog.leaflet.api.rest.response.file.FileListDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.FileBridgeService;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link FileBrowser}.
 *
 * @author Peter Smith
 */
@RunWith(JUnitParamsRunner.class)
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
    private static final String SUB1_SUB3 = "sub1/sub3";
    private static final String SUB1_SUB4 = "sub1/sub4";
    private static final String SUB2_SUB5= "sub2/sub5";
    private static final String SUB2_SUB5_SUB6 = "sub2/sub5/sub6";

    @Mock
    private FileBridgeService fileBridgeService;

    @Spy
    private URLUtilities urlUtilities;

    @InjectMocks
    private FileBrowser fileBrowser;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @Parameters(source = FileBrowserParameterProvider.class, method = "forFiles")
    public void shouldGetFilesUnderPath(String path, List<String> expectedFiles) throws CommunicationFailureException {

        // given
        given(fileBridgeService.getUploadedFiles()).willReturn(prepareFileListDataModel());

        // when
        List<FileDataModel> result = fileBrowser.getFiles(path);

        // then
        assertThat(result.size(), equalTo(expectedFiles.size()));
        assertThat(result.stream().map(FileDataModel::getPath).collect(Collectors.toList()).containsAll(expectedFiles), is(true));
    }

    @Test
    @Parameters(source = FileBrowserParameterProvider.class, method = "forFolders")
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
        return DirectoryListDataModel.getBuilder()
                .withItem(prepareDirectoryDataModel(ROOT_IMAGES))
                .withItem(prepareDirectoryDataModel(ROOT_OTHERS))
                .build();
    }

    private DirectoryDataModel prepareDirectoryDataModel(String root) {
        return DirectoryDataModel.getBuilder()
                .withRoot(root)
                .withChildren(Arrays.asList(SUB_1, SUB_2, SUB1_SUB3, SUB1_SUB4, SUB2_SUB5, SUB2_SUB5_SUB6))
                .build();
    }

    private FileListDataModel prepareFileListDataModel() {
        return FileListDataModel.getBuilder()
                .withItem(prepareFileDataModel(IMAGES_PART1_IMAGE1_JPG))
                .withItem(prepareFileDataModel(IMAGES_IMAGE2_JPG))
                .withItem(prepareFileDataModel(IMAGES_IMAGE3_JPG))
                .withItem(prepareFileDataModel(IMAGES_PART1_IMAGE4_JPG))
                .withItem(prepareFileDataModel(IMAGES_PART1_IMAGE5_JPG))
                .withItem(prepareFileDataModel(IMAGES_PART1_IMAGE6_JPG))
                .withItem(prepareFileDataModel(IMAGES_PART2_IMAGE7_JPG))
                .build();
    }

    private FileDataModel prepareFileDataModel(String path) {
        return FileDataModel.getBuilder()
                .withReference(String.format(REFERENCE_PATTERN, UUID.randomUUID()))
                .withPath(path)
                .build();
    }

    public static class FileBrowserParameterProvider {

        private static final String SUB_3 = "sub3";
        private static final String SUB_4 = "sub4";
        private static final String SUB_5 = "sub5";

        public static Object[] forFiles() {
            return new Object[] {
                    new Object[] {"images", Arrays.asList(IMAGES_IMAGE2_JPG, IMAGES_IMAGE3_JPG)},
                    new Object[] {"images/part1", Arrays.asList(IMAGES_PART1_IMAGE1_JPG, IMAGES_PART1_IMAGE4_JPG, IMAGES_PART1_IMAGE5_JPG, IMAGES_PART1_IMAGE6_JPG)},
                    new Object[] {"images/part2", Collections.singletonList(IMAGES_PART2_IMAGE7_JPG)},
                    new Object[] {"/images/", Arrays.asList(IMAGES_IMAGE2_JPG, IMAGES_IMAGE3_JPG)},
                    new Object[] {StringUtils.EMPTY, Collections.emptyList()},
                    new Object[] {"/non/existing/path", Collections.emptyList()},
                    new Object[] {null, Collections.emptyList()}
            };
        }

        public static Object[] forFolders() {
            return new Object[] {
                    new Object[] {null, Arrays.asList(ROOT_IMAGES, ROOT_OTHERS)},
                    new Object[] {StringUtils.EMPTY, Arrays.asList(ROOT_IMAGES, ROOT_OTHERS)},
                    new Object[] {ROOT_IMAGES, Arrays.asList(SUB_1, SUB_2)},
                    new Object[] {"/" + ROOT_IMAGES + "/", Arrays.asList(SUB_1, SUB_2)},
                    new Object[] {ROOT_IMAGES + "/" + SUB_1, Arrays.asList(SUB_3, SUB_4)},
                    new Object[] {ROOT_OTHERS + "/" + SUB_2, Collections.singletonList(SUB_5)},
                    new Object[] {"/" + ROOT_OTHERS + "/" + SUB_2 + "/", Collections.singletonList(SUB_5)},
                    new Object[] {ROOT_OTHERS + "/" + SUB2_SUB5_SUB6, Collections.emptyList()},
                    new Object[] {"non-existing-root", Collections.emptyList()},
                    new Object[] {"non-existing-root/non-existing-sub", Collections.emptyList()},
            };
        }
    }
}