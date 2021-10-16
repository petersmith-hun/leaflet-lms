package hu.psprog.leaflet.lms.service.facade.impl.utility;

import hu.psprog.leaflet.api.rest.response.file.FileDataModel;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link URLUtilities}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class URLUtilitiesTest {

    private static final String NORMALIZED_URL = "part1/part2/part3/part4";
    private static final String NON_NORMALIZED_URL = "/" + NORMALIZED_URL;
    private static final String PATH_IDENTIFIER = "dc63ced6-bf36-34da-ad3a-519c0041c316";
    private static final String NON_NORMALIZED_FILE_REFERENCE = PATH_IDENTIFIER + "/image.jpg";
    private static final String NORMALIZED_FILE_REFERENCE = "/" + NON_NORMALIZED_FILE_REFERENCE;

    @InjectMocks
    private URLUtilities urlUtilities;

    @Test
    public void shouldExtractSubPathByPattern() {

        // given
        String pattern = "/test/**";
        String url = "/test/to-extract";

        // when
        String result = urlUtilities.extractSubPath(pattern, url);

        // then
        assertThat(result, equalTo("to-extract"));
    }

    @Test
    public void shouldExtractionReturnEmptyStringIfNotMatching() {

        // given
        String pattern = "/test/**";
        String url = "/to-extract";

        // when
        String result = urlUtilities.extractSubPath(pattern, url);

        // then
        assertThat(result, equalTo(StringUtils.EMPTY));
    }

    @ParameterizedTest
    @MethodSource("extractionIntervalDataProvider")
    public void shouldExtractSubPathBySub(String source, int from, int to, String expected) {

        // when
        String result = urlUtilities.extractSubPath(from, to, source);

        // then
        assertThat(result, equalTo(expected));
    }

    @ParameterizedTest
    @MethodSource("filePathUUIDDataProvider")
    public void shouldExtractFilePathUUID(String reference) {

        // given
        FileDataModel fileDataModel = FileDataModel.getBuilder()
                .withReference(reference)
                .build();

        // when
        UUID result = urlUtilities.extractFilePathUUID(fileDataModel);

        // then
        assertThat(result, equalTo(UUID.fromString(PATH_IDENTIFIER)));
    }

    @ParameterizedTest
    @MethodSource("normalizationDataProvider")
    public void shouldNormalize(String source) {

        // when
        String result = urlUtilities.normalize(source);

        // then
        assertThat(result, equalTo(NORMALIZED_URL));
    }

    @ParameterizedTest
    @MethodSource("upUrlDataProvider")
    public void shouldGepUpUrl(String source, int disableAtLevel, String expected) {

        // when
        String result = urlUtilities.getUpURL(source, disableAtLevel);

        // then
        assertThat(result, equalTo(expected));
    }

    @ParameterizedTest
    @MethodSource("normalizationDataProvider")
    public void shouldSplitUrl(String source) {

        // when
        String[] result = urlUtilities.splitURL(source);

        // then
        assertThat(result.length, equalTo(4));
        assertThat(result, equalTo(NORMALIZED_URL.split("/")));
    }

    private static Stream<Arguments> upUrlDataProvider() {

        return Stream.of(
                Arguments.of(NORMALIZED_URL, 0, "part1/part2/part3"),
                Arguments.of("part1/part2/", 0, "part1"),
                Arguments.of("part1", 0, StringUtils.EMPTY),
                Arguments.of(StringUtils.EMPTY, 0, StringUtils.EMPTY),
                Arguments.of("part1/part2/part3", 3, "part1/part2/part3"),
                Arguments.of("part1/part2/part3/", 3, "part1/part2/part3"),
                Arguments.of("part1/part2/part3", 2, "part1/part2")
        );
    }

    private static Stream<Arguments> normalizationDataProvider() {

        return Stream.of(
                Arguments.of(NORMALIZED_URL),
                Arguments.of("/" + NORMALIZED_URL),
                Arguments.of(NORMALIZED_URL + "/"),
                Arguments.of("/" + NORMALIZED_URL + "/")
        );
    }

    private static Stream<Arguments> filePathUUIDDataProvider() {

        return Stream.of(
                Arguments.of(NON_NORMALIZED_FILE_REFERENCE),
                Arguments.of(NORMALIZED_FILE_REFERENCE)
        );
    }

    private static Stream<Arguments> extractionIntervalDataProvider() {

        return Stream.of(
                Arguments.of(NORMALIZED_URL, 2, 1, "part3"),
                Arguments.of(NON_NORMALIZED_URL, 2, 1, "part3"),
                Arguments.of(NORMALIZED_URL, 1, 1, "part2/part3"),
                Arguments.of(NON_NORMALIZED_URL, 1, 1, "part2/part3"),
                Arguments.of(NORMALIZED_URL, 0, 0, NORMALIZED_URL),
                Arguments.of(NORMALIZED_URL, 0, 2, "part1/part2"),
                Arguments.of(NORMALIZED_URL, 2, 0, "part3/part4"),
                Arguments.of(NORMALIZED_URL, 0, 5, StringUtils.EMPTY),
                Arguments.of(NORMALIZED_URL, 5, 0, StringUtils.EMPTY)
        );
    }
}