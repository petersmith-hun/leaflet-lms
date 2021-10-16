package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.routing.FrontEndRouteUpdateRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import hu.psprog.leaflet.lms.service.facade.FrontEndRoutingSupportFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link FrontEndRoutingManagementController}.
 *
 * @author Peter Smith
 */
@Extensions({
        @ExtendWith(MockitoExtension.class),
        @ExtendWith(SpringExtension.class)
})
public class FrontEndRoutingManagementControllerTest extends AbstractControllerTest {

    private static final String ROUTES = "routes";
    private static final long ROUTE_ID = 1L;
    private static final FrontEndRouteUpdateRequestModel FRONT_END_ROUTE_UPDATE_REQUEST_MODEL = new FrontEndRouteUpdateRequestModel();
    private static final String ROUTE_VIEW_PATH = "/system/routes/view/" + ROUTE_ID;
    private static final String ROUTES_PATH = "/system/routes";
    private static final String FIELD_ROUTES = "routes";
    private static final String FIELD_ROUTE = "route";
    private static final String PATH_ROUTE_CREATE = ROUTES_PATH + "/create";

    @Mock
    private FrontEndRoutingSupportFacade frontEndRoutingSupportFacade;

    @InjectMocks
    private FrontEndRoutingManagementController frontEndRoutingManagementController;

    @Test
    public void shouldListRoutes() throws CommunicationFailureException {

        // when
        frontEndRoutingManagementController.listRoutes();

        // then
        verify(frontEndRoutingSupportFacade).getRoutes();
        verifyViewCreated(VIEW_LIST);
        verifyFieldsSet(FIELD_ROUTES);
    }

    @Test
    public void shouldViewRoute() throws CommunicationFailureException {

        // when
        frontEndRoutingManagementController.viewRoute(ROUTE_ID);

        // then
        verify(frontEndRoutingSupportFacade).getRoute(ROUTE_ID);
        verifyViewCreated(VIEW_DETAILS);
        verifyFieldsSet(FIELD_ROUTE);
    }

    @Test
    public void shouldShowCreateRouteForm() {

        // when
        frontEndRoutingManagementController.showCreateRouteForm();

        // then
        verifyViewCreated(VIEW_EDIT_FORM);
    }

    @Test
    public void shouldProcessRouteCreation() throws CommunicationFailureException {

        // given
        given(frontEndRoutingSupportFacade.processCreateRoute(FRONT_END_ROUTE_UPDATE_REQUEST_MODEL)).willReturn(ROUTE_ID);

        // when
        frontEndRoutingManagementController.processRouteCreation(FRONT_END_ROUTE_UPDATE_REQUEST_MODEL, redirectAttributes);

        // then
        verifyFlashMessageSet();
        verifyRedirectionCreated(ROUTE_VIEW_PATH);
    }

    @Test
    public void shouldProcessRouteCreationHandleValidationFailure() throws CommunicationFailureException {

        // given
        doThrow(new ValidationFailureException(response)).when(frontEndRoutingSupportFacade).processCreateRoute(FRONT_END_ROUTE_UPDATE_REQUEST_MODEL);

        // when
        frontEndRoutingManagementController.processRouteCreation(FRONT_END_ROUTE_UPDATE_REQUEST_MODEL, redirectAttributes);

        // then
        verifyValidationViolationInfoSet(FRONT_END_ROUTE_UPDATE_REQUEST_MODEL);
        verifyRedirectionCreated(PATH_ROUTE_CREATE);
    }

    @Test
    public void shouldShowEditRouteForm() throws CommunicationFailureException {

        // when
        frontEndRoutingManagementController.showEditRouteForm(ROUTE_ID);

        // then
        verify(frontEndRoutingSupportFacade).getRoute(ROUTE_ID);
        verifyViewCreated(VIEW_EDIT_FORM);
        verifyFieldsSet(FIELD_ROUTE);
    }

    @Test
    public void shouldProcessRouteEditing() throws CommunicationFailureException {

        // when
        frontEndRoutingManagementController.processRouteEditing(ROUTE_ID, FRONT_END_ROUTE_UPDATE_REQUEST_MODEL, redirectAttributes);

        // then
        verify(frontEndRoutingSupportFacade).processEditRoute(ROUTE_ID, FRONT_END_ROUTE_UPDATE_REQUEST_MODEL);
        verifyFlashMessageSet();
        verifyRedirectionCreated(ROUTE_VIEW_PATH);
    }

    @Test
    public void shouldProcessRouteEditingHandleValidationFailure() throws CommunicationFailureException {

        // given
        doThrow(new ValidationFailureException(response)).when(frontEndRoutingSupportFacade).processEditRoute(ROUTE_ID, FRONT_END_ROUTE_UPDATE_REQUEST_MODEL);

        // when
        frontEndRoutingManagementController.processRouteEditing(ROUTE_ID, FRONT_END_ROUTE_UPDATE_REQUEST_MODEL, redirectAttributes);

        // then
        verifyValidationViolationInfoSet(FRONT_END_ROUTE_UPDATE_REQUEST_MODEL);
        verifyRedirectionCreated(ROUTE_VIEW_PATH);
    }

    @Test
    public void shouldProcessRouteStatusChangeWithEnabledMessage() throws CommunicationFailureException {

        // given
        given(frontEndRoutingSupportFacade.processStatusChange(ROUTE_ID)).willReturn(true);

        // when
        frontEndRoutingManagementController.processRouteStatusChange(ROUTE_ID, redirectAttributes);

        // then
        verify(frontEndRoutingSupportFacade).processStatusChange(ROUTE_ID);
        verifyStatusFlashMessage(true);
        verifyRedirectionCreated(ROUTE_VIEW_PATH);
    }

    @Test
    public void shouldProcessRouteStatusChangeWithDisabledMessage() throws CommunicationFailureException {

        // given
        given(frontEndRoutingSupportFacade.processStatusChange(ROUTE_ID)).willReturn(false);

        // when
        frontEndRoutingManagementController.processRouteStatusChange(ROUTE_ID, redirectAttributes);

        // then
        verify(frontEndRoutingSupportFacade).processStatusChange(ROUTE_ID);
        verifyStatusFlashMessage(false);
        verifyRedirectionCreated(ROUTE_VIEW_PATH);
    }

    @Test
    public void shouldProcessRouteDeletion() throws CommunicationFailureException {

        // when
        frontEndRoutingManagementController.processRouteDeletion(ROUTE_ID, redirectAttributes);

        // then
        verify(frontEndRoutingSupportFacade).processDeleteRoute(ROUTE_ID);
        verifyFlashMessageSet();
        verifyRedirectionCreated(ROUTES_PATH);
    }

    @Override
    String controllerViewGroup() {
        return ROUTES;
    }
}