package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.comment.CommentUpdateRequestModel;
import hu.psprog.leaflet.api.rest.response.comment.CommentListDataModel;
import hu.psprog.leaflet.api.rest.response.comment.ExtendedCommentDataModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.bridge.client.domain.OrderBy;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.facade.CommentFacade;
import hu.psprog.leaflet.lms.web.controller.pagination.CommentPaginationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static hu.psprog.leaflet.lms.web.controller.pagination.PaginationHelper.PARAMETER_LIMIT;
import static hu.psprog.leaflet.lms.web.controller.pagination.PaginationHelper.PARAMETER_ORDER_BY;
import static hu.psprog.leaflet.lms.web.controller.pagination.PaginationHelper.PARAMETER_ORDER_DIRECTION;
import static hu.psprog.leaflet.lms.web.controller.pagination.PaginationHelper.PARAMETER_PAGE;

/**
 * Comments management controller for LMS.
 *
 * @author Peter Smith
 */
@Controller
@RequestMapping(CommentsController.PATH_COMMENTS)
public class CommentsController extends BaseController {

    private static final String VIEW_COMMENTS_LIST = "view/comments/list";
    private static final String VIEW_COMMENTS_DETAILS = "view/comments/details";
    private static final String VIEW_COMMENTS_EDIT_FORM = "view/comments/edit_form";
    private static final String VIEW_COMMENTS_DELETE_FORM = "view/comments/delete_form";

    private static final String COMMENT_SUCCESSFULLY_UPDATED = "Comment successfully updated.";
    private static final String COMMENT_SUCCESSFULLY_DELETED = "Comment successfully deleted (%s)";
    private static final String COMMENT_STATUS_SUCCESSFULLY_CHANGED = "Comment status successfully changed to %s";

    private static final String PATH_LIST_FOR_ENTRY = "/{id}";
    private static final String PATH_LIST_FOR_ENTRY_WITH_PAGE = "/{id}/{page}";

    static final String PATH_COMMENTS = "/comments";

    private CommentFacade commentFacade;
    private CommentPaginationHelper paginationHelper;

    @Autowired
    public CommentsController(CommentFacade commentFacade, CommentPaginationHelper paginationHelper) {
        this.commentFacade = commentFacade;
        this.paginationHelper = paginationHelper;
    }

    /**
     * Returns paginated list of comments created under given entry.
     *
     * @param entryID ID of the entry to return comments belong to
     * @param page page number (page indexing starts at 1)
     * @param limit number of entries on one page
     * @param orderBy order by {@link OrderBy.Comment} options
     * @param orderDirection order direction (ASC|DESC)
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = {PATH_LIST_FOR_ENTRY, PATH_LIST_FOR_ENTRY_WITH_PAGE})
    public ModelAndView listCommentsForEntry(@PathVariable(PATH_VARIABLE_ID) Long entryID,
                                             @PathVariable(value = PARAMETER_PAGE, required = false) Optional<Integer> page,
                                             @RequestParam(value = PARAMETER_LIMIT, required = false) Optional<Integer> limit,
                                             @RequestParam(value = PARAMETER_ORDER_BY, required = false) Optional<String> orderBy,
                                             @RequestParam(value = PARAMETER_ORDER_DIRECTION, required = false) Optional<String> orderDirection)
            throws CommunicationFailureException {

        WrapperBodyDataModel<CommentListDataModel> result = commentFacade.getCommentsForEntry(entryID, paginationHelper.extractPage(page),
                paginationHelper.getLimit(limit), paginationHelper.mapOrderBy(orderBy), paginationHelper.mapOrderDirection(orderDirection));

        return modelAndViewFactory
                .createForView(VIEW_COMMENTS_LIST)
                .withAttribute("content", result.getBody())
                .withAttribute("pagination", result.getPagination())
                .build();
    }

    /**
     * Returns comment identified by given ID.
     *
     * @param commentID ID of the comment to return
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_VIEW)
    public ModelAndView viewComment(@PathVariable(PATH_VARIABLE_ID) Long commentID) throws CommunicationFailureException {

        return modelAndViewFactory
                .createForView(VIEW_COMMENTS_DETAILS)
                .withAttribute("comment", commentFacade.getComment(commentID))
                .build();
    }

    /**
     * Renders moderation (editor) form for comment identified by given ID.
     *
     * @param commentID ID of the comment to edit
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_EDIT)
    public ModelAndView showEditCommentForm(@PathVariable(PATH_VARIABLE_ID) Long commentID) throws CommunicationFailureException {

        return modelAndViewFactory
                .createForView(VIEW_COMMENTS_EDIT_FORM)
                .withAttribute("comment", commentFacade.getComment(commentID))
                .build();
    }

    /**
     * Processes comment edit request.
     *
     * @param commentID ID of the comment to edit
     * @param commentUpdateRequestModel updated comment data
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object (redirection to edited comment)
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_EDIT)
    public ModelAndView processEditComment(@PathVariable(PATH_VARIABLE_ID) Long commentID,
                                           @ModelAttribute CommentUpdateRequestModel commentUpdateRequestModel,
                                           RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        commentFacade.processEditComment(commentID, commentUpdateRequestModel);
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, COMMENT_SUCCESSFULLY_UPDATED);

        return modelAndViewFactory
                .createRedirectionTo(getRedirectionPath(commentID));
    }

    /**
     * Renders comment deletion form.
     *
     * @param commentID ID of the comment to delete
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_DELETE)
    public ModelAndView showDeleteCommentForm(@PathVariable(PATH_VARIABLE_ID) Long commentID) throws CommunicationFailureException {

        return modelAndViewFactory
                .createForView(VIEW_COMMENTS_DELETE_FORM)
                .withAttribute("comment", commentFacade.getComment(commentID))
                .build();
    }

    /**
     * Processes comment deletion request.
     * User can decide whether to delete the comment permanently of only logically.
     *
     * @param commentID ID of the comment to delete
     * @param permanent delete comment permanently on {@code true}; delete logically otherwise
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object (redirection to comment list under entry)
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_DELETE)
    public ModelAndView processDeleteComment(@PathVariable(PATH_VARIABLE_ID) Long commentID,
                                             @ModelAttribute(value = "permanent") Optional<Boolean> permanent,
                                             RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        boolean deletePermanently = permanent
                .orElse(false);

        ExtendedCommentDataModel commentDataModel = commentFacade.getComment(commentID);
        commentFacade.processDeleteComment(commentID, deletePermanently);
        String deletionMessageIsPermanent = deletePermanently
                ? "permanently"
                : "logically";
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, String.format(COMMENT_SUCCESSFULLY_DELETED, deletionMessageIsPermanent));

        return modelAndViewFactory
                .createRedirectionTo(getRedirectionToCommentList(commentDataModel));
    }

    /**
     * Processes comment status change request.
     *
     * @param commentID ID of the comment to change status of
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object (redirection to edited comment)
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_STATUS)
    public ModelAndView processStatusChange(@PathVariable(PATH_VARIABLE_ID) Long commentID, RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        String currentStatus = commentFacade.processStatusChange(commentID)
                ? "enabled"
                : "disabled";
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, String.format(COMMENT_STATUS_SUCCESSFULLY_CHANGED, currentStatus));

        return modelAndViewFactory
                .createRedirectionTo(getRedirectionPath(commentID));
    }

    private String getRedirectionToCommentList(ExtendedCommentDataModel commentDataModel) throws CommunicationFailureException {
        return PATH_COMMENTS + "/" + commentDataModel.getAssociatedEntry().getId();
    }

    private String getRedirectionPath(Long commentID) {
        return PATH_COMMENTS + replaceIDInViewPath(commentID);
    }
}
