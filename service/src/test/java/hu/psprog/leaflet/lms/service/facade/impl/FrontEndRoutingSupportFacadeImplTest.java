package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.routing.FrontEndRouteUpdateRequestModel;
import hu.psprog.leaflet.api.rest.response.routing.ExtendedFrontEndRouteDataModel;
import hu.psprog.leaflet.api.rest.response.routing.ExtendedFrontEndRouteListDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.FrontEndRoutingSupportBridgeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link FrontEndRoutingSupportFacadeImpl}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class FrontEndRoutingSupportFacadeImplTest {

    private static final long ROUTE_ID = 1L;
    private static final FrontEndRouteUpdateRequestModel FRONT_END_ROUTE_UPDATE_REQUEST_MODEL = new FrontEndRouteUpdateRequestModel();
    private static final ExtendedFrontEndRouteDataModel EXTENDED_FRONT_END_ROUTE_DATA_MODEL = prepareExtendedFrontEndRouteDataModel();
    private static final ExtendedFrontEndRouteListDataModel EXTENDED_FRONT_END_ROUTE_LIST_DATA_MODEL = prepareExtendedFrontEndRouteListDataModel();

    @Mock
    private FrontEndRoutingSupportBridgeService frontEndRoutingSupportBridgeService;

    @InjectMocks
    private FrontEndRoutingSupportFacadeImpl frontEndRoutingSupportFacade;

    @Test
    public void shouldGetRoutes() throws CommunicationFailureException {

        // given
        given(frontEndRoutingSupportBridgeService.getAllRoutes()).willReturn(EXTENDED_FRONT_END_ROUTE_LIST_DATA_MODEL);

        // when
        List<ExtendedFrontEndRouteDataModel> result = frontEndRoutingSupportFacade.getRoutes();

        // then
        assertThat(result, notNullValue());
        assertThat(result, equalTo(Collections.singletonList(EXTENDED_FRONT_END_ROUTE_DATA_MODEL)));
    }

    @Test
    public void shouldGetRoute() throws CommunicationFailureException {

        // given
        given(frontEndRoutingSupportBridgeService.getRouteByID(ROUTE_ID)).willReturn(EXTENDED_FRONT_END_ROUTE_DATA_MODEL);

        // when
        ExtendedFrontEndRouteDataModel result = frontEndRoutingSupportFacade.getRoute(ROUTE_ID);

        // then
        assertThat(result, equalTo(EXTENDED_FRONT_END_ROUTE_DATA_MODEL));
    }

    @Test
    public void shouldProcessCreateRoute() throws CommunicationFailureException {

        // given
        given(frontEndRoutingSupportBridgeService.createRoute(FRONT_END_ROUTE_UPDATE_REQUEST_MODEL)).willReturn(EXTENDED_FRONT_END_ROUTE_DATA_MODEL);

        // when
        Long result = frontEndRoutingSupportFacade.processCreateRoute(FRONT_END_ROUTE_UPDATE_REQUEST_MODEL);

        // then
        assertThat(result, equalTo(ROUTE_ID));
    }

    @Test
    public void shouldProcessEditRoute() throws CommunicationFailureException {

        // when
        frontEndRoutingSupportFacade.processEditRoute(ROUTE_ID, FRONT_END_ROUTE_UPDATE_REQUEST_MODEL);

        // then
        verify(frontEndRoutingSupportBridgeService).updateRoute(ROUTE_ID, FRONT_END_ROUTE_UPDATE_REQUEST_MODEL);
    }

    @Test
    public void shouldProcessStatusChange() throws CommunicationFailureException {

        // given
        given(frontEndRoutingSupportBridgeService.changeStatus(ROUTE_ID)).willReturn(EXTENDED_FRONT_END_ROUTE_DATA_MODEL);

        // when
        boolean result = frontEndRoutingSupportFacade.processStatusChange(ROUTE_ID);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void shouldProcessDeleteRoute() throws CommunicationFailureException {

        // when
        frontEndRoutingSupportFacade.processDeleteRoute(ROUTE_ID);

        // then
        verify(frontEndRoutingSupportBridgeService).deleteRoute(ROUTE_ID);
    }

    private static ExtendedFrontEndRouteDataModel prepareExtendedFrontEndRouteDataModel() {
        return ExtendedFrontEndRouteDataModel.getExtendedBuilder()
                .withId(ROUTE_ID)
                .withRouteId("route-id")
                .withEnabled(true)
                .build();
    }

    private static ExtendedFrontEndRouteListDataModel prepareExtendedFrontEndRouteListDataModel() {
        return ExtendedFrontEndRouteListDataModel.getExtendedBuilder()
                .withItem(EXTENDED_FRONT_END_ROUTE_DATA_MODEL)
                .build();
    }
}