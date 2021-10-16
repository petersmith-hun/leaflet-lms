package hu.psprog.leaflet.lms.service.conversion;

import hu.psprog.leaflet.api.rest.request.entry.EntryCreateRequestModel;
import hu.psprog.leaflet.api.rest.request.entry.EntryInitialStatus;
import hu.psprog.leaflet.lms.service.domain.entry.ModifyEntryRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@link ModifyEntryRequestToEntryCreateRequestModelConverter}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class ModifyEntryRequestToEntryCreateRequestModelConverterTest {

    private static final long CATEGORY_ID = 2L;
    private static final boolean IS_ENABLED = true;
    private static final String LINK = "entry-link-1";
    private static final Locale LOCALE = Locale.ENGLISH;
    private static final String META_DESCRIPTION = "meta desc";
    private static final String META_KEYWORDS = "meta keywords";
    private static final String META_TITLE = "meta title";
    private static final String PROLOGUE = "prologue";
    private static final String RAW_CONTENT = "raw content";
    private static final EntryInitialStatus STATUS = EntryInitialStatus.REVIEW;
    private static final String ENTRY_TITLE = "entry title";
    private static final long USER_ID = 3L;

    @InjectMocks
    private ModifyEntryRequestToEntryCreateRequestModelConverter converter;

    @Test
    public void shouldConvert() {

        // given
        ModifyEntryRequest request = new ModifyEntryRequest();
        request.setCategoryID(CATEGORY_ID);
        request.setEnabled(IS_ENABLED);
        request.setLink(LINK);
        request.setLocale(LOCALE);
        request.setMetaDescription(META_DESCRIPTION);
        request.setMetaKeywords(META_KEYWORDS);
        request.setMetaTitle(META_TITLE);
        request.setPrologue(PROLOGUE);
        request.setRawContent(RAW_CONTENT);
        request.setStatus(STATUS);
        request.setTitle(ENTRY_TITLE);
        request.setUserID(USER_ID);

        // when
        EntryCreateRequestModel result = converter.convert(request);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getCategoryID(), equalTo(CATEGORY_ID));
        assertThat(result.isEnabled(), equalTo(IS_ENABLED));
        assertThat(result.getLink(), equalTo(LINK));
        assertThat(result.getLocale(), equalTo(LOCALE));
        assertThat(result.getMetaDescription(), equalTo(META_DESCRIPTION));
        assertThat(result.getMetaKeywords(), equalTo(META_KEYWORDS));
        assertThat(result.getMetaTitle(), equalTo(META_TITLE));
        assertThat(result.getPrologue(), equalTo(PROLOGUE));
        assertThat(result.getRawContent(), equalTo(RAW_CONTENT));
        assertThat(result.getStatus(), equalTo(STATUS));
        assertThat(result.getTitle(), equalTo(ENTRY_TITLE));
        assertThat(result.getUserID(), equalTo(USER_ID));
    }
}