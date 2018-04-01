package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.lms.service.domain.translations.TranslationPackUploadRequestModel;
import hu.psprog.leaflet.translation.api.domain.TranslationPack;
import hu.psprog.leaflet.translation.api.domain.TranslationPackCreationRequest;
import hu.psprog.leaflet.translation.client.TranslationServiceClient;
import hu.psprog.leaflet.translation.client.impl.exception.TranslationPackCreationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link TranslationManagementFacadeImpl}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class TranslationManagementFacadeImplTest {

    private static final UUID PACK_ID = UUID.randomUUID();
    private static final TranslationPackUploadRequestModel TRANSLATION_PACK_UPLOAD_REQUEST_MODEL = new TranslationPackUploadRequestModel();
    private static final String PACK_NAME = "pack1";
    private static final Locale LOCALE = Locale.ENGLISH;
    private static final String TEST_CSV = "test-csv";
    private static final String MOCK_CSV_CONTENT = "key1;value1\nkey2;value2\nkey3;value3\ninvalid\ninvalid,invalid,invalid";
    private static final String INVALID_MOCK_CSV_CONTENT = "key1,value1\nkey2,value2\nkey3,value3";
    private static final MockMultipartFile MOCK_MULTIPART_FILE = new MockMultipartFile(TEST_CSV, MOCK_CSV_CONTENT.getBytes());
    private static final MockMultipartFile INVALID_MOCK_MULTIPART_FILE = new MockMultipartFile(TEST_CSV, INVALID_MOCK_CSV_CONTENT.getBytes());
    private static final Map<String, String> EXPECTED_DEFINITION_MAP = new HashMap<>();

    static {
        TRANSLATION_PACK_UPLOAD_REQUEST_MODEL.setPackName(PACK_NAME);
        TRANSLATION_PACK_UPLOAD_REQUEST_MODEL.setLocale(LOCALE);

        EXPECTED_DEFINITION_MAP.put("key1", "value1");
        EXPECTED_DEFINITION_MAP.put("key2", "value2");
        EXPECTED_DEFINITION_MAP.put("key3", "value3");
    }

    @Mock
    private TranslationServiceClient translationServiceClient;

    @Mock
    private MultipartFile multipartFile;

    @Captor
    private ArgumentCaptor<TranslationPackCreationRequest> translationPackCreationRequest;

    @InjectMocks
    private TranslationManagementFacadeImpl translationManagementFacade;

    @Test
    public void shouldGetPacks() {

        // when
        translationManagementFacade.getPacks();

        // then
        verify(translationServiceClient).listStoredPacks();
    }

    @Test
    public void shouldGetPack() {

        // when
        translationManagementFacade.getPack(PACK_ID);

        // then
        verify(translationServiceClient).getPackByID(PACK_ID);
    }

    @Test
    public void shouldProcessCreatePack() {

        // given
        TRANSLATION_PACK_UPLOAD_REQUEST_MODEL.setDefinitions(MOCK_MULTIPART_FILE);
        given(translationServiceClient.createTranslationPack(any(TranslationPackCreationRequest.class)))
                .willReturn(TranslationPack.getPackBuilder().withId(PACK_ID).build());

        // when
        UUID result = translationManagementFacade.processCreatePack(TRANSLATION_PACK_UPLOAD_REQUEST_MODEL);

        // then
        assertThat(result, equalTo(PACK_ID));
        verify(translationServiceClient).createTranslationPack(translationPackCreationRequest.capture());
        assertThat(translationPackCreationRequest.getValue().getPackName(), equalTo(PACK_NAME));
        assertThat(translationPackCreationRequest.getValue().getLocale(), equalTo(LOCALE));
        assertThat(translationPackCreationRequest.getValue().getDefinitions(), equalTo(EXPECTED_DEFINITION_MAP));
    }

    @Test(expected = TranslationPackCreationException.class)
    public void shouldProcessCreatePackWithExceptionOnInvalidInputCSV() {

        // given
        TRANSLATION_PACK_UPLOAD_REQUEST_MODEL.setDefinitions(INVALID_MOCK_MULTIPART_FILE);

        // when
        translationManagementFacade.processCreatePack(TRANSLATION_PACK_UPLOAD_REQUEST_MODEL);

        // then
        // exception expected
    }

    @Test(expected = TranslationPackCreationException.class)
    public void shouldProcessCreatePackWithExceptionOnInputCSVReadFailure() throws IOException {

        // given
        doThrow(IOException.class).when(multipartFile).getInputStream();
        TranslationPackUploadRequestModel translationPackUploadRequestModel = new TranslationPackUploadRequestModel();
        translationPackUploadRequestModel.setDefinitions(multipartFile);

        // when
        translationManagementFacade.processCreatePack(translationPackUploadRequestModel);

        // then
        // exception expected
    }

    @Test
    public void shouldProcessDeletePack() {

        // when
        translationManagementFacade.processDeletePack(PACK_ID);

        // then
        verify(translationServiceClient).deleteTranslationPack(PACK_ID);
    }

    @Test
    public void shouldProcessChangePackStatusWithEnabledResult() {

        // given
        given(translationServiceClient.changePackStatus(PACK_ID)).willReturn(TranslationPack.getPackBuilder().withEnabled(true).build());

        // when
        boolean result = translationManagementFacade.processChangePackStatus(PACK_ID);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void shouldProcessChangePackStatusWithDisabledResult() {

        // given
        given(translationServiceClient.changePackStatus(PACK_ID)).willReturn(TranslationPack.getPackBuilder().withEnabled(false).build());

        // when
        boolean result = translationManagementFacade.processChangePackStatus(PACK_ID);

        // then
        assertThat(result, is(false));
    }
}