package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.tag.TagAssignmentRequestModel;
import hu.psprog.leaflet.api.rest.request.tag.TagCreateRequestModel;
import hu.psprog.leaflet.api.rest.response.entry.EditEntryDataModel;
import hu.psprog.leaflet.api.rest.response.tag.TagDataModel;
import hu.psprog.leaflet.api.rest.response.tag.TagListDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.TagBridgeService;
import hu.psprog.leaflet.lms.service.domain.entry.ModifyEntryRequest;
import hu.psprog.leaflet.lms.service.util.EntityConnectionDifferenceCalculator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link TagFacadeImpl}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class TagFacadeImplTest {

    private static final String TAG_NAME = "tag-1";
    private static final Long ID_TO_ATTACH = 1L;
    private static final Long ID_TO_DETACH = 2L;
    private static final Long TAG_ID = 3L;
    private static final Long ENTRY_ID = 5L;
    private static final TagCreateRequestModel TAG_CREATE_REQUEST_MODEL = new TagCreateRequestModel();

    @Mock
    private EntityConnectionDifferenceCalculator entityConnectionDifferenceCalculator;

    @Mock
    private TagBridgeService tagBridgeService;

    @Mock
    private EntityConnectionDifferenceCalculator.EntityConnectionContext<Long, TagDataModel> context;

    @InjectMocks
    private TagFacadeImpl tagFacade;

    @Test
    public void shouldGetAllTags() throws CommunicationFailureException {

        // given
        given(tagBridgeService.getAllTags()).willReturn(prepareTagListDataModel());

        // when
        List<TagDataModel> result = tagFacade.getAllTags();

        // then
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getName(), equalTo(TAG_NAME));
    }

    @Test
    public void shouldGetAllTagsReturnEmptyListOnEmptyResponse() throws CommunicationFailureException {

        // given
        given(tagBridgeService.getAllTags()).willReturn(TagListDataModel.getBuilder().build());

        // when
        List<TagDataModel> result = tagFacade.getAllTags();

        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void shouldHandleAssignmentsOnChange() throws CommunicationFailureException {

        // given
        ModifyEntryRequest modifyEntryRequest = new ModifyEntryRequest();
        EditEntryDataModel editEntryDataModel = EditEntryDataModel.getExtendedBuilder()
                .withId(ENTRY_ID)
                .build();
        given(entityConnectionDifferenceCalculator.createContextFor(eq(modifyEntryRequest.getTags()), eq(editEntryDataModel.getTags()), any())).willReturn(context);
        given(context.collectForAttach()).willReturn(Collections.singletonList(ID_TO_ATTACH));
        given(context.collectForDetach()).willReturn(Collections.singletonList(ID_TO_DETACH));

        // when
        tagFacade.handleAssignmentsOnChange(modifyEntryRequest, editEntryDataModel);

        // then
        verify(tagBridgeService).attachTag(prepareTagAssignmentRequestModel(ID_TO_ATTACH));
        verify(tagBridgeService).detachTag(prepareTagAssignmentRequestModel(ID_TO_DETACH));
    }

    @Test
    public void shouldGetTag() throws CommunicationFailureException {

        // given
        given(tagBridgeService.getTag(TAG_ID)).willReturn(prepareTagDataModel());

        // when
        TagDataModel result = tagFacade.getTag(TAG_ID);

        // then
        assertThat(result.getName(), equalTo(TAG_NAME));
    }

    @Test
    public void shouldProcessCreateTag() throws CommunicationFailureException {

        // given
        given(tagBridgeService.createTag(TAG_CREATE_REQUEST_MODEL)).willReturn(prepareTagDataModel());

        // when
        Long result = tagFacade.processCreateTag(TAG_CREATE_REQUEST_MODEL);

        // then
        assertThat(result, equalTo(TAG_ID));
    }

    @Test
    public void shouldProcessCreateTagReturnNullOnFailure() throws CommunicationFailureException {

        // given
        given(tagBridgeService.createTag(TAG_CREATE_REQUEST_MODEL)).willReturn(null);

        // when
        Long result = tagFacade.processCreateTag(TAG_CREATE_REQUEST_MODEL);

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldProcessEditTag() throws CommunicationFailureException {

        // when
        tagFacade.processEditTag(TAG_ID, TAG_CREATE_REQUEST_MODEL);

        // then
        verify(tagBridgeService).updateTag(TAG_ID, TAG_CREATE_REQUEST_MODEL);
    }

    @Test
    public void shouldProcessStatusChange() throws CommunicationFailureException {

        // given
        given(tagBridgeService.changeStatus(TAG_ID)).willReturn(prepareTagDataModel());

        // when
        boolean result = tagFacade.processStatusChange(TAG_ID);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void shouldProcessDeleteTag() throws CommunicationFailureException {

        // when
        tagFacade.processDeleteTag(TAG_ID);

        // then
        verify(tagBridgeService).deleteTag(TAG_ID);
    }

    private TagAssignmentRequestModel prepareTagAssignmentRequestModel(Long tagID) {

        TagAssignmentRequestModel tagAssignmentRequestModel = new TagAssignmentRequestModel();
        tagAssignmentRequestModel.setEntryID(ENTRY_ID);
        tagAssignmentRequestModel.setTagID(tagID);

        return tagAssignmentRequestModel;
    }

    private TagListDataModel prepareTagListDataModel() {
        return TagListDataModel.getBuilder()
                .withItem(prepareTagDataModel())
                .build();
    }

    private TagDataModel prepareTagDataModel() {
        return TagDataModel.getBuilder()
                .withId(TAG_ID)
                .withName(TAG_NAME)
                .withEnabled(true)
                .build();
    }
}