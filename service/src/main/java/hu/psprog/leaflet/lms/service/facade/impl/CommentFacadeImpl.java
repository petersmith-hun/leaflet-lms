package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.comment.CommentSearchParameters;
import hu.psprog.leaflet.api.rest.request.comment.CommentUpdateRequestModel;
import hu.psprog.leaflet.api.rest.response.comment.CommentListDataModel;
import hu.psprog.leaflet.api.rest.response.comment.ExtendedCommentDataModel;
import hu.psprog.leaflet.api.rest.response.comment.ExtendedCommentListDataModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EntryDataModel;
import hu.psprog.leaflet.bridge.client.domain.OrderBy;
import hu.psprog.leaflet.bridge.client.domain.OrderDirection;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.CommentBridgeService;
import hu.psprog.leaflet.lms.service.facade.CommentFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of {@link CommentFacade}.
 *
 * @author Peter Smith
 */
@Service
public class CommentFacadeImpl implements CommentFacade {

    private static final int COMMENT_PAGE_SIZE_FOR_PENDING_CHECK = 100;

    private final CommentBridgeService commentBridgeService;

    @Autowired
    public CommentFacadeImpl(CommentBridgeService commentBridgeService) {
        this.commentBridgeService = commentBridgeService;
    }

    @Override
    public WrapperBodyDataModel<CommentListDataModel> getCommentsForEntry(Long entryID, int page, int limit, OrderBy.Comment orderBy, OrderDirection orderDirection)
            throws CommunicationFailureException {
        return commentBridgeService.getPageOfCommentsForEntry(entryID, page, limit, orderBy, orderDirection);
    }

    @Override
    public ExtendedCommentDataModel getComment(Long commentID) throws CommunicationFailureException {
        return commentBridgeService.getComment(commentID);
    }

    @Override
    public WrapperBodyDataModel<ExtendedCommentListDataModel> getPendingComments(int page, int limit) throws CommunicationFailureException {

        var commentSearchParameters = new CommentSearchParameters();
        commentSearchParameters.setPage(page);
        commentSearchParameters.setLimit(limit);
        commentSearchParameters.setEnabled(false);
        commentSearchParameters.setDeleted(false);

        return commentBridgeService.searchComments(commentSearchParameters);
    }

    @Override
    public Map<Long, Long> getNumberOfPendingCommentsByEntry() throws CommunicationFailureException {

        var page = 1;
        var entryIDsWithPendingComment = new LinkedList<Long>();
        WrapperBodyDataModel<ExtendedCommentListDataModel> commentSearchResult;

        do {
            commentSearchResult = getPendingComments(page++, COMMENT_PAGE_SIZE_FOR_PENDING_CHECK);
            entryIDsWithPendingComment.addAll(collectEntryIDs(commentSearchResult));

        } while (commentSearchResult.pagination().hasNext());

        return entryIDsWithPendingComment.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    @Override
    public Long getNumberOfPendingCommentsForEntry(Long entryID) throws CommunicationFailureException {

        return getNumberOfPendingCommentsByEntry()
                .getOrDefault(entryID, 0L);
    }

    @Override
    public void processEditComment(Long commentID, CommentUpdateRequestModel commentUpdateRequestModel) throws CommunicationFailureException {
        commentBridgeService.updateComment(commentID, commentUpdateRequestModel);
    }

    @Override
    public void processDeleteComment(Long commentID, boolean isPermanent) throws CommunicationFailureException {

        if (isPermanent) {
            commentBridgeService.deleteCommentPermanently(commentID);
        } else {
            commentBridgeService.deleteCommentLogically(commentID);
        }
    }

    @Override
    public boolean processStatusChange(Long commentID) throws CommunicationFailureException {
        return commentBridgeService.changeStatus(commentID).enabled();
    }

    private List<Long> collectEntryIDs(WrapperBodyDataModel<ExtendedCommentListDataModel> commentSearchResult) {

        return commentSearchResult.body()
                .comments().stream()
                .map(ExtendedCommentDataModel::associatedEntry)
                .map(EntryDataModel::id)
                .collect(Collectors.toList());
    }
}
