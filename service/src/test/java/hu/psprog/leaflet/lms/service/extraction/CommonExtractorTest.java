package hu.psprog.leaflet.lms.service.extraction;

import hu.psprog.leaflet.api.rest.response.file.FileDataModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link CommonExtractor}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class CommonExtractorTest {

    private static final String PATH_IDENTIFIER = "dc63ced6-bf36-34da-ad3a-519c0041c316";
    private static final String FILENAME = "image.jpg";
    private static final String CHARACTER_SLASH = "/";

    @InjectMocks
    private CommonExtractor commonExtractor;

    @Test
    public void shouldExtractFromWellFormedReferenceString() {

        // given
        String reference = prepareReference(true);

        // when
        UUID result = commonExtractor.extractPathUUIDFromReference(reference);

        // then
        assertThat(result, equalTo(UUID.fromString(PATH_IDENTIFIER)));
    }

    @Test
    public void shouldExtractFromWellFormedReferenceStringInFileDataModel() {

        // given
        FileDataModel fileDataModel = FileDataModel.getBuilder()
                .withReference(prepareReference(true))
                .build();

        // when
        UUID result = commonExtractor.extractPathUUIDFromReference(fileDataModel);

        // then
        assertThat(result, equalTo(UUID.fromString(PATH_IDENTIFIER)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWithNonWellFormedReferenceString() {

        // given
        String reference = prepareReference(false);

        // when
        commonExtractor.extractPathUUIDFromReference(reference);

        // then
        // exception expected
    }

    private String prepareReference(boolean wellFormed) {

        String reference;
        if (wellFormed) {
            reference = CHARACTER_SLASH + PATH_IDENTIFIER + CHARACTER_SLASH + FILENAME;
        } else {
            reference = PATH_IDENTIFIER + CHARACTER_SLASH + FILENAME;
        }

        return reference;
    }
}