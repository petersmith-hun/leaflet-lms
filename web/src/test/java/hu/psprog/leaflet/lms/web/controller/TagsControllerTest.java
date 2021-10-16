package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.tag.TagCreateRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import hu.psprog.leaflet.lms.service.facade.TagFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link TagsController}.
 *
 * @author Peter Smith
 */
@Extensions({
        @ExtendWith(MockitoExtension.class),
        @ExtendWith(SpringExtension.class)
})
public class TagsControllerTest extends AbstractControllerTest {

    private static final String TAGS = "tags";
    private static final long TAG_ID = 6L;
    private static final String TAG_VIEW_PATH = "/tags/view/" + TAG_ID;
    private static final String FIELD_TAG = "tag";
    private static final String PATH_TAGS = "/tags";
    private static final String PATH_TAGS_CREATE = PATH_TAGS + "/create";

    @Mock
    private TagFacade tagFacade;

    @InjectMocks
    private TagsController tagsController;

    @Test
    public void shouldListTags() throws CommunicationFailureException {

        // when
        tagsController.listTags();

        // then
        verify(tagFacade).getAllTags();
        verifyViewCreated(VIEW_LIST);
        verifyFieldsSet(TAGS);
    }

    @Test
    public void shouldViewTag() throws CommunicationFailureException {

        // when
        tagsController.viewTag(TAG_ID);

        // then
        verify(tagFacade).getTag(TAG_ID);
        verifyViewCreated(VIEW_DETAILS);
        verifyFieldsSet(FIELD_TAG);
    }

    @Test
    public void shouldShowCreateTagForm() {

        // when
        tagsController.showCreateTagForm();

        // then
        verifyViewCreated(VIEW_EDIT_FORM);
    }

    @Test
    public void shouldProcessCreateTag() throws CommunicationFailureException {

        // given
        TagCreateRequestModel tagCreateRequestModel = new TagCreateRequestModel();
        given(tagFacade.processCreateTag(tagCreateRequestModel)).willReturn(TAG_ID);

        // when
        tagsController.processCreateTag(tagCreateRequestModel, redirectAttributes);

        // then
        verify(tagFacade).processCreateTag(tagCreateRequestModel);
        verifyFlashMessageSet();
        verifyRedirectionCreated(TAG_VIEW_PATH);
    }

    @Test
    public void shouldProcessCreateTagHandleValidationFailure() throws CommunicationFailureException {

        // given
        TagCreateRequestModel tagCreateRequestModel = new TagCreateRequestModel();
        doThrow(new ValidationFailureException(response)).when(tagFacade).processCreateTag(tagCreateRequestModel);

        // when
        tagsController.processCreateTag(tagCreateRequestModel, redirectAttributes);

        // then
        verifyValidationViolationInfoSet(tagCreateRequestModel);
        verifyRedirectionCreated(PATH_TAGS_CREATE);
    }

    @Test
    public void shouldShowEditTagForm() throws CommunicationFailureException {

        // when
        tagsController.showEditTagForm(TAG_ID);

        // then
        verify(tagFacade).getTag(TAG_ID);
        verifyViewCreated(VIEW_EDIT_FORM);
        verifyFieldsSet(FIELD_TAG);
    }

    @Test
    public void shouldProcessEditTag() throws CommunicationFailureException {

        // given
        TagCreateRequestModel tagCreateRequestModel = new TagCreateRequestModel();

        // when
        tagsController.processEditTag(TAG_ID, tagCreateRequestModel, redirectAttributes);

        // then
        verify(tagFacade).processEditTag(TAG_ID, tagCreateRequestModel);
        verifyFlashMessageSet();
        verifyRedirectionCreated(TAG_VIEW_PATH);
    }

    @Test
    public void shouldProcessEditTagHandleValidationFailure() throws CommunicationFailureException {

        // given
        TagCreateRequestModel tagCreateRequestModel = new TagCreateRequestModel();
        doThrow(new ValidationFailureException(response)).when(tagFacade).processEditTag(TAG_ID, tagCreateRequestModel);

        // when
        tagsController.processEditTag(TAG_ID, tagCreateRequestModel, redirectAttributes);

        // then
        verifyValidationViolationInfoSet(tagCreateRequestModel);
        verifyRedirectionCreated(TAG_VIEW_PATH);
    }

    @Test
    public void shouldProcessDeleteTag() throws CommunicationFailureException {

        // when
        tagsController.processDeleteTag(TAG_ID, redirectAttributes);

        // then
        verify(tagFacade).processDeleteTag(TAG_ID);
        verifyFlashMessageSet();
        verifyRedirectionCreated(PATH_TAGS);
    }

    @Test
    public void shouldProcessChangeStatusWithEnabledStatusMessage() throws CommunicationFailureException {

        // given
        given(tagFacade.processStatusChange(TAG_ID)).willReturn(true);

        // when
        tagsController.processStatusChange(TAG_ID, redirectAttributes);

        // then
        verify(tagFacade).processStatusChange(TAG_ID);
        verifyStatusFlashMessage(true);
        verifyRedirectionCreated(TAG_VIEW_PATH);
    }

    @Test
    public void shouldProcessChangeStatusWithDisabledStatusMessage() throws CommunicationFailureException {

        // given
        given(tagFacade.processStatusChange(TAG_ID)).willReturn(false);

        // when
        tagsController.processStatusChange(TAG_ID, redirectAttributes);

        // then
        verify(tagFacade).processStatusChange(TAG_ID);
        verifyStatusFlashMessage(false);
        verifyRedirectionCreated(TAG_VIEW_PATH);
    }

    @Override
    String controllerViewGroup() {
        return TAGS;
    }
}