package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.comment.CommentUpdateRequestModel;
import hu.psprog.leaflet.api.rest.response.comment.CommentListDataModel;
import hu.psprog.leaflet.api.rest.response.comment.ExtendedCommentDataModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EntryDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import hu.psprog.leaflet.lms.service.facade.CommentFacade;
import hu.psprog.leaflet.lms.web.controller.pagination.CommentPaginationHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link CommentsController}.
 *
 * @author Peter Smith
 */
@Extensions({
        @ExtendWith(MockitoExtension.class),
        @ExtendWith(SpringExtension.class)
})
public class CommentsControllerTest extends AbstractControllerTest {

    private static final String COMMENTS = "comments";
    private static final Long ENTRY_ID = 2L;
    private static final Long COMMENT_ID = 3L;
    private static final String COMMENT_VIEW_PATH = "/comments/view/" + COMMENT_ID;
    private static final String DELETE_REDIRECTION_PATH = "/comments/" + ENTRY_ID;
    private static final String FIELD_COMMENT = "comment";
    private static final WrapperBodyDataModel<CommentListDataModel> WRAPPER_BODY_DATA_MODEL = WrapperBodyDataModel.<CommentListDataModel>getBuilder().build();

    @Mock
    private CommentFacade commentFacade;

    @Mock
    private CommentPaginationHelper commentPaginationHelper;

    @Mock
    private HttpServletRequest request;

    private CommentsController commentsController;

    @BeforeEach
    public void setup() {
        super.setup();
        commentsController = new CommentsController(modelAndViewFactory, commentFacade, commentPaginationHelper);
    }

    @Test
    public void shouldListCommentsForEntry() throws CommunicationFailureException {

        // given
        given(commentPaginationHelper.extractPage(PAGE)).willReturn(PAGE.get());
        given(commentPaginationHelper.getLimit(LIMIT)).willReturn(LIMIT.get());
        given(commentFacade.getCommentsForEntry(eq(ENTRY_ID), eq(PAGE.get()), eq(LIMIT.get()), any(), any()))
                .willReturn(WRAPPER_BODY_DATA_MODEL);

        // when
        commentsController.listCommentsForEntry(ENTRY_ID, PAGE, LIMIT, Optional.empty(), Optional.empty(), request);

        // then
        verify(commentFacade).getCommentsForEntry(eq(ENTRY_ID), eq(PAGE.get()), eq(LIMIT.get()), any(), any());
        verify(commentPaginationHelper).extractPaginationAttributes(WRAPPER_BODY_DATA_MODEL, request);
        verifyViewCreated(VIEW_LIST);
        verifyFieldsSet(FIELD_CONTENT, FIELD_PAGINATION);
    }

    @Test
    public void shouldViewComment() throws CommunicationFailureException {

        // when
        commentsController.viewComment(COMMENT_ID);

        // then
        verify(commentFacade).getComment(COMMENT_ID);
        verifyViewCreated(VIEW_DETAILS);
        verifyFieldsSet(FIELD_COMMENT);
    }

    @Test
    public void shouldShowEditCommentForm() throws CommunicationFailureException {

        // when
        commentsController.showEditCommentForm(COMMENT_ID);

        // then
        verify(commentFacade).getComment(COMMENT_ID);
        verifyViewCreated(VIEW_EDIT_FORM);
        verifyFieldsSet(FIELD_COMMENT);
    }

    @Test
    public void shouldProcessEditComment() throws CommunicationFailureException {

        // given
        CommentUpdateRequestModel commentUpdateRequestModel = new CommentUpdateRequestModel();

        // when
        commentsController.processEditComment(COMMENT_ID, commentUpdateRequestModel, redirectAttributes);

        // then
        verify(commentFacade).processEditComment(COMMENT_ID, commentUpdateRequestModel);
        verifyFlashMessageSet();
        verifyRedirectionCreated(COMMENT_VIEW_PATH);
    }

    @Test
    public void shouldProcessEditCommentHandleValidationFailure() throws CommunicationFailureException {

        // given
        CommentUpdateRequestModel commentUpdateRequestModel = new CommentUpdateRequestModel();
        doThrow(new ValidationFailureException(response)).when(commentFacade).processEditComment(COMMENT_ID, commentUpdateRequestModel);

        // when
        commentsController.processEditComment(COMMENT_ID, commentUpdateRequestModel, redirectAttributes);

        // then
        verifyValidationViolationInfoSet(commentUpdateRequestModel);
        verifyRedirectionCreated(COMMENT_VIEW_PATH);
    }

    @Test
    public void shouldShowDeleteCommentForm() throws CommunicationFailureException {

        // when
        commentsController.showDeleteCommentForm(COMMENT_ID);

        // then
        verify(commentFacade).getComment(COMMENT_ID);
        verifyViewCreated(VIEW_DELETE_FORM);
        verifyFieldsSet(FIELD_COMMENT);
    }

    @Test
    public void shouldProcessDeleteCommentWithPermanentDeletion() throws CommunicationFailureException {

        // given
        given(commentFacade.getComment(COMMENT_ID)).willReturn(prepareExtendedCommentDataModel());

        // when
        commentsController.processDeleteComment(COMMENT_ID, Optional.of(true), redirectAttributes);

        // then
        verifyCommentDeletion(true);
    }

    @Test
    public void shouldProcessDeleteCommentWithLogicalDeletion() throws CommunicationFailureException {

        // given
        given(commentFacade.getComment(COMMENT_ID)).willReturn(prepareExtendedCommentDataModel());

        // when
        commentsController.processDeleteComment(COMMENT_ID, Optional.of(false), redirectAttributes);

        // then
        verifyCommentDeletion(false);
    }

    @Test
    public void shouldProcessDeleteCommentWithLogicalDeletionOnMissingParameter() throws CommunicationFailureException {

        // given
        given(commentFacade.getComment(COMMENT_ID)).willReturn(prepareExtendedCommentDataModel());

        // when
        commentsController.processDeleteComment(COMMENT_ID, Optional.empty(), redirectAttributes);

        // then
        verifyCommentDeletion(false);
    }

    @Test
    public void shouldProcessChangeStatusWithEnabledStatusMessage() throws CommunicationFailureException {

        // given
        given(commentFacade.processStatusChange(COMMENT_ID)).willReturn(true);

        // when
        commentsController.processStatusChange(COMMENT_ID, redirectAttributes);

        // then
        verify(commentFacade).processStatusChange(COMMENT_ID);
        verifyStatusFlashMessage(true);
        verifyRedirectionCreated(COMMENT_VIEW_PATH);
    }

    @Test
    public void shouldProcessChangeStatusWithDisabledStatusMessage() throws CommunicationFailureException {

        // given
        given(commentFacade.processStatusChange(COMMENT_ID)).willReturn(false);

        // when
        commentsController.processStatusChange(COMMENT_ID, redirectAttributes);

        // then
        verify(commentFacade).processStatusChange(COMMENT_ID);
        verifyStatusFlashMessage(false);
        verifyRedirectionCreated(COMMENT_VIEW_PATH);
    }

    private void verifyCommentDeletion(boolean permanent) throws CommunicationFailureException {
        verify(commentFacade).getComment(COMMENT_ID);
        verify(commentFacade).processDeleteComment(COMMENT_ID, permanent);
        verifyDeletionFlashMessage(permanent);
        verifyRedirectionCreated(DELETE_REDIRECTION_PATH);
    }

    private ExtendedCommentDataModel prepareExtendedCommentDataModel() {
        return ExtendedCommentDataModel.getBuilder()
                .withAssociatedEntry(EntryDataModel.getBuilder()
                        .withId(ENTRY_ID)
                        .build())
                .build();
    }

    @Override
    String controllerViewGroup() {
        return COMMENTS;
    }
}