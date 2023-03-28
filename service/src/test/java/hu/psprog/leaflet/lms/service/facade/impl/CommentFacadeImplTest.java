package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.comment.CommentSearchParameters;
import hu.psprog.leaflet.api.rest.request.comment.CommentUpdateRequestModel;
import hu.psprog.leaflet.api.rest.response.comment.CommentListDataModel;
import hu.psprog.leaflet.api.rest.response.comment.ExtendedCommentDataModel;
import hu.psprog.leaflet.api.rest.response.comment.ExtendedCommentListDataModel;
import hu.psprog.leaflet.api.rest.response.common.PaginationDataModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EditEntryDataModel;
import hu.psprog.leaflet.bridge.client.domain.OrderBy;
import hu.psprog.leaflet.bridge.client.domain.OrderDirection;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.CommentBridgeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.stream.Stream;

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
@ExtendWith(MockitoExtension.class)
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
                        .withItem(prepareExtendedCommentDataModel(1))
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
        ExtendedCommentDataModel response = prepareExtendedCommentDataModel(1);
        given(commentBridgeService.getComment(COMMENT_ID)).willReturn(response);

        // when
        ExtendedCommentDataModel result = commentFacade.getComment(COMMENT_ID);

        // then
        assertThat(result, equalTo(response));
    }

    @Test
    public void shouldGetPendingComments() throws CommunicationFailureException {

        // given
        int pageNumber = 1;
        int limit = 30;
        CommentSearchParameters commentSearchParameters = createSearchParameters(pageNumber, limit);
        WrapperBodyDataModel<ExtendedCommentListDataModel> response = createSearchResponse(false);

        given(commentBridgeService.searchComments(commentSearchParameters)).willReturn(response);

        // when
        WrapperBodyDataModel<ExtendedCommentListDataModel> result = commentFacade.getPendingComments(pageNumber, limit);

        // then
        assertThat(result, equalTo(response));
    }

    @Test
    public void shouldGetNumberOfPendingCommentsByEntry() throws CommunicationFailureException {

        // given
        int limit = 100;

        given(commentBridgeService.searchComments(createSearchParameters(1, limit))).willReturn(createSearchResponse(true));
        given(commentBridgeService.searchComments(createSearchParameters(2, limit))).willReturn(createSearchResponse(true));
        given(commentBridgeService.searchComments(createSearchParameters(3, limit))).willReturn(createSearchResponse(false));

        // when
        Map<Long, Long> result = commentFacade.getNumberOfPendingCommentsByEntry();

        // then
        assertThat(result, equalTo(Map.of(
                1L, 3L,
                2L, 9L,
                3L, 6L
        )));
    }

    @ParameterizedTest
    @MethodSource("pendingCommentCountDataProvider")
    public void shouldGetNumberOfPendingCommentsForEntry(long entryID, long commentCount) throws CommunicationFailureException {

        // given
        given(commentBridgeService.searchComments(createSearchParameters(1, 100))).willReturn(createSearchResponse(false));

        // when
        Long result = commentFacade.getNumberOfPendingCommentsForEntry(entryID);

        // then
        assertThat(result, equalTo(commentCount));
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
        given(commentBridgeService.changeStatus(COMMENT_ID)).willReturn(prepareExtendedCommentDataModel(1));

        // when
        boolean result = commentFacade.processStatusChange(COMMENT_ID);

        // then
        assertThat(result, is(true));
    }

    private ExtendedCommentDataModel prepareExtendedCommentDataModel(long entryID) {

        return ExtendedCommentDataModel.getExtendedBuilder()
                .withContent(CONTENT)
                .withEnabled(true)
                .withAssociatedEntry(EditEntryDataModel.getBuilder()
                        .withId(entryID)
                        .build())
                .build();
    }

    private CommentSearchParameters createSearchParameters(int pageNumber, int limit) {

        var commentSearchParameters = new CommentSearchParameters();
        commentSearchParameters.setPage(pageNumber);
        commentSearchParameters.setLimit(limit);
        commentSearchParameters.setEnabled(false);
        commentSearchParameters.setDeleted(false);

        return commentSearchParameters;
    }

    private WrapperBodyDataModel createSearchResponse(boolean hasNextPage) {

        return WrapperBodyDataModel.getBuilder()
                .withBody(ExtendedCommentListDataModel.getBuilder()
                        .withItem(prepareExtendedCommentDataModel(1))
                        .withItem(prepareExtendedCommentDataModel(2))
                        .withItem(prepareExtendedCommentDataModel(2))
                        .withItem(prepareExtendedCommentDataModel(2))
                        .withItem(prepareExtendedCommentDataModel(3))
                        .withItem(prepareExtendedCommentDataModel(3))
                        .build())
                .withPagination(PaginationDataModel.getBuilder()
                        .withHasNext(hasNextPage)
                        .build())
                .build();
    }

    private static Stream<Arguments> pendingCommentCountDataProvider() {

        return Stream.of(
                Arguments.of(1L, 1L),
                Arguments.of(2L, 3L),
                Arguments.of(3L, 2L),
                Arguments.of(4L, 0L)
        );
    }
}