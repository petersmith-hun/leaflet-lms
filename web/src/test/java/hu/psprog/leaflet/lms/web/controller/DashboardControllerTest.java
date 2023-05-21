package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.facade.CommentFacade;
import hu.psprog.leaflet.lms.service.facade.DashboardFacade;
import hu.psprog.leaflet.lms.web.auth.mock.WithMockedJWTUser;
import hu.psprog.leaflet.lms.web.controller.pagination.CommentPaginationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link DashboardController}.
 *
 * @author Peter Smith
 */
@Extensions({
        @ExtendWith(MockitoExtension.class),
        @ExtendWith(SpringExtension.class)
})
@WithMockedJWTUser(userID = 5L)
public class DashboardControllerTest extends AbstractControllerTest {

    private static final String VIEW_GROUP = "dashboard";
    private static final String VIEW_HOME = "home";
    private static final String FIELD_REGISTERED_SERVICES = "registeredServices";
    private static final String FIELD_STACK_ADMIN_CLIENT = "stackAdminClient";
    private static final String FIELD_PENDING_COMMENTS = "pendingComments";
    private static final String FIELD_PAGINATION = "pagination";

    @Mock
    private CommentFacade commentFacade;

    @Mock
    private DashboardFacade dashboardFacade;

    @Mock
    private CommentPaginationHelper paginationHelper;

    private DashboardController dashboardController;

    @BeforeEach
    public void setup() {
        super.setup();
        dashboardController = new DashboardController(modelAndViewFactory, commentFacade, dashboardFacade, paginationHelper);
    }

    @Test
    public void shouldHomeRenderDashboard() throws CommunicationFailureException {

        // given
        WrapperBodyDataModel dataModel = WrapperBodyDataModel.getBuilder().build();

        given(commentFacade.getPendingComments(1, 3)).willReturn(dataModel);

        // when
        dashboardController.home(1, request);

        // then
        verify(dashboardFacade).getRegisteredServices();
        verify(paginationHelper).extractPaginationAttributes(dataModel, request);
        verifyViewCreated(VIEW_HOME);
        verifyFieldsSet(FIELD_REGISTERED_SERVICES, FIELD_PENDING_COMMENTS, FIELD_PAGINATION, FIELD_STACK_ADMIN_CLIENT);
    }

    @Override
    String controllerViewGroup() {
        return VIEW_GROUP;
    }
}
