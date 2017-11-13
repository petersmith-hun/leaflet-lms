package hu.psprog.leaflet.lms.service.facade;

import hu.psprog.leaflet.api.rest.request.comment.CommentUpdateRequestModel;
import hu.psprog.leaflet.api.rest.response.comment.CommentListDataModel;
import hu.psprog.leaflet.api.rest.response.comment.ExtendedCommentDataModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.bridge.client.domain.OrderBy;
import hu.psprog.leaflet.bridge.client.domain.OrderDirection;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;

/**
 * Comment operations facade.
 *
 * @author Peter Smith
 */
public interface CommentFacade {

    /**
     * Returns paginated list of comments for given entry.
     *
     * @param entryID ID of the entry to return comments for
     * @param page page number (page indexing starts at 1)
     * @param limit number of comments on one page
     * @param orderBy order by {@link OrderBy.Comment} options
     * @param orderDirection order direction (ASC|DESC)
     * @return page of comments wrapped in {@link WrapperBodyDataModel<CommentListDataModel>} object
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    WrapperBodyDataModel<CommentListDataModel> getCommentsForEntry(Long entryID, int page, int limit, OrderBy.Comment orderBy, OrderDirection orderDirection)
            throws CommunicationFailureException;

    /**
     * Returns comment identified by given ID.
     *
     * @param commentID ID of the comment to retrieve
     * @return comment data as {@link ExtendedCommentDataModel}
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    ExtendedCommentDataModel getComment(Long commentID) throws CommunicationFailureException;

    /**
     * Processes comment update request.
     *
     * @param commentID ID of the comment to update
     * @param commentUpdateRequestModel updated comment data
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    void processEditComment(Long commentID, CommentUpdateRequestModel commentUpdateRequestModel) throws CommunicationFailureException;

    /**
     * Processes comment deletion request.
     * Permanent / logical deletion can be switched with the isPermanent parameter.
     *
     * @param commentID ID of the comment to delete
     * @param isPermanent pass {@code true} to permanently delete comment; {@code false} for logical-only deletion
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    void processDeleteComment(Long commentID, boolean isPermanent) throws CommunicationFailureException;

    /**
     * Processes comment status change request.
     *
     * @param commentID ID of the comment to change status of
     * @return current status as boolean, {@code true} if enabled, {@code false} otherwise
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    boolean processStatusChange(Long commentID) throws CommunicationFailureException;
}
