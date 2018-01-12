package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.tag.TagCreateRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.facade.TagFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link TagsController}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class TagsControllerTest extends AbstractControllerTest {

    private static final String TAGS = "tags";
    private static final long TAG_ID = 6L;
    private static final String TAG_VIEW_PATH = "/tags/view/" + TAG_ID;

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
        verifyViewCreated("list");
        verifyFieldsSet("tags");
    }

    @Test
    public void shouldViewTag() throws CommunicationFailureException {

        // when
        tagsController.viewTag(TAG_ID);

        // then
        verify(tagFacade).getTag(TAG_ID);
        verifyViewCreated("details");
        verifyFieldsSet("tag");
    }

    @Test
    public void shouldShowCreateTagForm() {

        // when
        tagsController.showCreateTagForm();

        // then
        verifyViewCreated("edit_form");
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
    public void shouldShowEditTagForm() throws CommunicationFailureException {

        // when
        tagsController.showEditTagForm(TAG_ID);

        // then
        verify(tagFacade).getTag(TAG_ID);
        verifyViewCreated("edit_form");
        verifyFieldsSet("tag");
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
    public void shouldProcessDeleteTag() throws CommunicationFailureException {

        // when
        tagsController.processDeleteTag(TAG_ID, redirectAttributes);

        // then
        verify(tagFacade).processDeleteTag(TAG_ID);
        verifyFlashMessageSet();
        verifyRedirectionCreated("/tags");
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