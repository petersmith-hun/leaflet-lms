package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import hu.psprog.leaflet.lms.service.domain.translations.TranslationPackUploadRequestModel;
import hu.psprog.leaflet.lms.service.facade.TranslationManagementFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link TranslationManagementController}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class TranslationManagementControllerTest extends AbstractControllerTest {

    private static final TranslationPackUploadRequestModel TRANSLATION_PACK_UPLOAD_REQUEST_MODEL = new TranslationPackUploadRequestModel();
    private static final String TRANSLATIONS = "translations";
    private static final UUID PACK_ID = UUID.randomUUID();

    private static final String FIELD_PACKS = "packs";
    private static final String FIELD_PACK = "pack";
    private static final String PATH_TRANSLATIONS = "/system/translations";
    private static final String PATH_TRANSLATIONS_VIEW = "/system/translations/view/" + PACK_ID;
    private static final String PATH_TRANSLATIONS_CREATE = PATH_TRANSLATIONS + "/create";

    @Mock
    private TranslationManagementFacade translationManagementFacade;

    @InjectMocks
    private TranslationManagementController translationManagementController;

    @Test
    public void shouldListPacks() throws CommunicationFailureException {

        // when
        translationManagementController.listPacks();

        // then
        verify(translationManagementFacade).getPacks();
        verifyViewCreated(VIEW_LIST);
        verifyFieldsSet(FIELD_PACKS);
    }

    @Test
    public void shouldViewPackDetails() throws CommunicationFailureException {

        // when
        translationManagementController.viewPackDetails(PACK_ID);

        // then
        verify(translationManagementFacade).getPack(PACK_ID);
        verifyViewCreated(VIEW_DETAILS);
        verifyFieldsSet(FIELD_PACK);
    }

    @Test
    public void shouldShowCreatePackForm() {

        // when
        translationManagementController.showCreatePackForm();

        // then
        verifyViewCreated(VIEW_EDIT_FORM);
    }

    @Test
    public void shouldProcessPackCreation() throws CommunicationFailureException {

        // given
        given(translationManagementFacade.processCreatePack(TRANSLATION_PACK_UPLOAD_REQUEST_MODEL)).willReturn(PACK_ID);

        // when
        translationManagementController.processPackCreation(TRANSLATION_PACK_UPLOAD_REQUEST_MODEL, redirectAttributes);

        // when
        verifyFlashMessageSet();
        verifyRedirectionCreated(PATH_TRANSLATIONS_VIEW);
    }

    @Test
    public void shouldProcessPackCreationHandleValidationFailure() throws CommunicationFailureException {

        // given
        given(translationManagementFacade.processCreatePack(TRANSLATION_PACK_UPLOAD_REQUEST_MODEL)).willReturn(PACK_ID);
        doThrow(new ValidationFailureException(response)).when(translationManagementFacade).processCreatePack(TRANSLATION_PACK_UPLOAD_REQUEST_MODEL);

        // when
        translationManagementController.processPackCreation(TRANSLATION_PACK_UPLOAD_REQUEST_MODEL, redirectAttributes);

        // when
        verifyValidationViolationInfoSet(TRANSLATION_PACK_UPLOAD_REQUEST_MODEL);
        verifyRedirectionCreated(PATH_TRANSLATIONS_CREATE);
    }

    @Test
    public void shouldProcessStatusChangeWithEnabledStatusMessage() throws CommunicationFailureException {

        // given
        given(translationManagementFacade.processChangePackStatus(PACK_ID)).willReturn(true);

        // when
        translationManagementController.processStatusChange(PACK_ID, redirectAttributes);

        // then
        verifyStatusFlashMessage(true);
        verifyRedirectionCreated(PATH_TRANSLATIONS_VIEW);
    }

    @Test
    public void shouldProcessStatusChangeWithDisabledStatusMessage() throws CommunicationFailureException {

        // given
        given(translationManagementFacade.processChangePackStatus(PACK_ID)).willReturn(false);

        // when
        translationManagementController.processStatusChange(PACK_ID, redirectAttributes);

        // then
        verifyStatusFlashMessage(false);
        verifyRedirectionCreated(PATH_TRANSLATIONS_VIEW);
    }

    @Test
    public void shouldProcessPackDeletion() throws CommunicationFailureException {

        // when
        translationManagementController.processPackDeletion(PACK_ID, redirectAttributes);

        // then
        verifyFlashMessageSet();
        verifyRedirectionCreated(PATH_TRANSLATIONS);
    }

    @Override
    String controllerViewGroup() {
        return TRANSLATIONS;
    }
}