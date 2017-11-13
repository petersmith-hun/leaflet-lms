package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.comment.CommentUpdateRequestModel;
import hu.psprog.leaflet.api.rest.response.comment.CommentListDataModel;
import hu.psprog.leaflet.api.rest.response.comment.ExtendedCommentDataModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.bridge.client.domain.OrderBy;
import hu.psprog.leaflet.bridge.client.domain.OrderDirection;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.CommentBridgeService;
import hu.psprog.leaflet.lms.service.facade.CommentFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link CommentFacade}.
 *
 * @author Peter Smith
 */
@Service
public class CommentFacadeImpl implements CommentFacade {

    private CommentBridgeService commentBridgeService;

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
        return commentBridgeService.changeStatus(commentID).isEnabled();
    }
}
