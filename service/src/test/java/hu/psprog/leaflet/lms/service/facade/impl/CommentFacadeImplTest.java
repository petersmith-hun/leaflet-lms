package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.comment.CommentUpdateRequestModel;
import hu.psprog.leaflet.api.rest.response.comment.CommentListDataModel;
import hu.psprog.leaflet.api.rest.response.comment.ExtendedCommentDataModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.bridge.client.domain.OrderBy;
import hu.psprog.leaflet.bridge.client.domain.OrderDirection;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.CommentBridgeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link CommentFacadeImpl}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class CommentFacadeImplTest {

    private static final Long COMMENT_ID = 3L;
    private static final long ENTRY_ID = 1L;
    private static final int PAGE = 1;
    private static final int LIMIT = 10;
    private static final OrderBy.Comment ORDER_BY = OrderBy.Comment.CREATED;
    private static final OrderDirection ORDER_DIRECTION = OrderDirection.ASC;
    private static final String CONTENT = "comment";

    @Mock
    private CommentBridgeService commentBridgeService;

    @InjectMocks
    private CommentFacadeImpl commentFacade;

    @Test
    public void shouldGetCommentsForEntry() throws CommunicationFailureException {

        // given
        WrapperBodyDataModel<CommentListDataModel> response = WrapperBodyDataModel.getBuilder()
                .withBody(CommentListDataModel.getBuilder()
                        .withItem(prepareExtendedCommentDataModel())
                        .build())
                .build();
        given(commentBridgeService.getPageOfCommentsForEntry(ENTRY_ID, PAGE, LIMIT, ORDER_BY, ORDER_DIRECTION)).willReturn(response);

        // when
        WrapperBodyDataModel<CommentListDataModel> result = commentFacade.getCommentsForEntry(ENTRY_ID, PAGE, LIMIT, ORDER_BY, ORDER_DIRECTION);

        // then
        assertThat(result, equalTo(response));
        verify(commentBridgeService).getPageOfCommentsForEntry(ENTRY_ID, PAGE, LIMIT, ORDER_BY, ORDER_DIRECTION);
    }

    @Test
    public void shouldGetComment() throws CommunicationFailureException {

        // given
        ExtendedCommentDataModel response = prepareExtendedCommentDataModel();
        given(commentBridgeService.getComment(COMMENT_ID)).willReturn(response);

        // when
        ExtendedCommentDataModel result = commentFacade.getComment(COMMENT_ID);

        // then
        assertThat(result, equalTo(response));
    }

    @Test
    public void shouldProcessEditComment() throws CommunicationFailureException {

        // given
        CommentUpdateRequestModel commentUpdateRequestModel = new CommentUpdateRequestModel();
        commentUpdateRequestModel.setContent(CONTENT);

        // when
        commentFacade.processEditComment(COMMENT_ID, commentUpdateRequestModel);

        // then
        verify(commentBridgeService).updateComment(COMMENT_ID, commentUpdateRequestModel);
    }

    @Test
    public void shouldProcessPermanentDeletion() throws CommunicationFailureException {

        // when
        commentFacade.processDeleteComment(COMMENT_ID, true);

        // then
        commentBridgeService.deleteCommentPermanently(COMMENT_ID);
    }

    @Test
    public void shouldProcessLogicalDeletion() throws CommunicationFailureException {

        // when
        commentFacade.processDeleteComment(COMMENT_ID, false);

        // then
        commentBridgeService.deleteCommentLogically(COMMENT_ID);
    }

    @Test
    public void shouldProcessStatusChange() throws CommunicationFailureException {

        // given
        given(commentBridgeService.changeStatus(COMMENT_ID)).willReturn(prepareExtendedCommentDataModel());

        // when
        boolean result = commentFacade.processStatusChange(COMMENT_ID);

        // then
        assertThat(result, is(true));
    }

    private ExtendedCommentDataModel prepareExtendedCommentDataModel() {
        return ExtendedCommentDataModel.getExtendedBuilder()
                .withContent(CONTENT)
                .withEnabled(true)
                .build();
    }
}