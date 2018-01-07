package hu.psprog.leaflet.lms.service.facade.impl.utility;

import hu.psprog.leaflet.api.rest.response.file.FileDataModel;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link URLUtilities}.
 *
 * @author Peter Smith
 */
@RunWith(JUnitParamsRunner.class)
public class URLUtilitiesTest {

    private static final String NORMALIZED_URL = "part1/part2/part3/part4";
    private static final String NON_NORMALIZED_URL = "/" + NORMALIZED_URL;
    private static final String PATH_IDENTIFIER = "dc63ced6-bf36-34da-ad3a-519c0041c316";
    private static final String NON_NORMALIZED_FILE_REFERENCE = PATH_IDENTIFIER + "/image.jpg";
    private static final String NORMALIZED_FILE_REFERENCE = "/" + NON_NORMALIZED_FILE_REFERENCE;

    @InjectMocks
    private URLUtilities urlUtilities;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

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

    @Test
    @Parameters(source = ExtractionIntervalParameterProvider.class)
    public void shouldExtractSubPathBySub(String source, int from, int to, String expected) {

        // when
        String result = urlUtilities.extractSubPath(from, to, source);

        // then
        assertThat(result, equalTo(expected));
    }

    @Test
    @Parameters(source = FilePathUUIDParameterProvider.class)
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

    @Test
    @Parameters(source = NormalizationParameterProvider.class)
    public void shouldNormalize(String source) {

        // when
        String result = urlUtilities.normalize(source);

        // then
        assertThat(result, equalTo(NORMALIZED_URL));
    }

    @Test
    @Parameters(source = UpUrlParameterProvider.class)
    public void shouldGepUpUrl(String source, int disableAtLevel, String expected) {

        // when
        String result = urlUtilities.getUpURL(source, disableAtLevel);

        // then
        assertThat(result, equalTo(expected));
    }

    @Test
    @Parameters(source = NormalizationParameterProvider.class)
    public void shouldSplitUrl(String source) {

        // when
        String[] result = urlUtilities.splitURL(source);

        // then
        assertThat(result.length, equalTo(4));
        assertThat(result, equalTo(NORMALIZED_URL.split("/")));
    }

    public static class UpUrlParameterProvider {

        public static Object[] provide() {
            return new Object[] {
                    new Object[] {NORMALIZED_URL, 0, "part1/part2/part3"},
                    new Object[] {"part1/part2/", 0, "part1"},
                    new Object[] {"part1", 0, StringUtils.EMPTY},
                    new Object[] {StringUtils.EMPTY, 0, StringUtils.EMPTY},
                    new Object[] {"part1/part2/part3", 3, "part1/part2/part3"},
                    new Object[] {"part1/part2/part3/", 3, "part1/part2/part3"},
                    new Object[] {"part1/part2/part3", 2, "part1/part2"},
            };
        }
    }

    public static class NormalizationParameterProvider {

        public static Object[] provide() {
            return new Object[] {
                    new Object[] {NORMALIZED_URL},
                    new Object[] {"/" + NORMALIZED_URL},
                    new Object[] {NORMALIZED_URL + "/"},
                    new Object[] {"/" + NORMALIZED_URL + "/"}
            };
        }
    }

    public static class FilePathUUIDParameterProvider {

        public static Object[] provide() {
            return new Object[] {
                    new Object[] {NON_NORMALIZED_FILE_REFERENCE},
                    new Object[] {NORMALIZED_FILE_REFERENCE}
            };
        }
    }

    public static class ExtractionIntervalParameterProvider {

        public static Object[] provide() {
            return new Object[] {
                    new Object[] {NORMALIZED_URL, 2, 1, "part3"},
                    new Object[] {NON_NORMALIZED_URL, 2, 1, "part3"},
                    new Object[] {NORMALIZED_URL, 1, 1, "part2/part3"},
                    new Object[] {NON_NORMALIZED_URL, 1, 1, "part2/part3"},
                    new Object[] {NORMALIZED_URL, 0, 0, NORMALIZED_URL},
                    new Object[] {NORMALIZED_URL, 0, 2, "part1/part2"},
                    new Object[] {NORMALIZED_URL, 2, 0, "part3/part4"},
                    new Object[] {NORMALIZED_URL, 0, 5, StringUtils.EMPTY},
                    new Object[] {NORMALIZED_URL, 5, 0, StringUtils.EMPTY},
            };
        }
    }
}