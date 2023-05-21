package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.routing.FrontEndRouteUpdateRequestModel;
import hu.psprog.leaflet.api.rest.response.routing.ExtendedFrontEndRouteDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.FrontEndRoutingSupportBridgeService;
import hu.psprog.leaflet.lms.service.facade.FrontEndRoutingSupportFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of {@link FrontEndRoutingSupportFacade}.
 *
 * @author Peter Smith
 */
@Service
public class FrontEndRoutingSupportFacadeImpl implements FrontEndRoutingSupportFacade {

    private final FrontEndRoutingSupportBridgeService frontEndRoutingSupportBridgeService;

    @Autowired
    public FrontEndRoutingSupportFacadeImpl(FrontEndRoutingSupportBridgeService frontEndRoutingSupportBridgeService) {
        this.frontEndRoutingSupportBridgeService = frontEndRoutingSupportBridgeService;
    }

    @Override
    public List<ExtendedFrontEndRouteDataModel> getRoutes() throws CommunicationFailureException {
        return Optional.ofNullable(frontEndRoutingSupportBridgeService.getAllRoutes().routes())
                .map(routes -> routes.stream()
                        .map(ExtendedFrontEndRouteDataModel.class::cast)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public ExtendedFrontEndRouteDataModel getRoute(Long routeID) throws CommunicationFailureException {
        return frontEndRoutingSupportBridgeService.getRouteByID(routeID);
    }

    @Override
    public Long processCreateRoute(FrontEndRouteUpdateRequestModel frontEndRouteUpdateRequestModel) throws CommunicationFailureException {
        return Optional.ofNullable(frontEndRoutingSupportBridgeService.createRoute(frontEndRouteUpdateRequestModel))
                .map(ExtendedFrontEndRouteDataModel::id)
                .orElse(null);
    }

    @Override
    public void processEditRoute(Long routeID, FrontEndRouteUpdateRequestModel frontEndRouteUpdateRequestModel) throws CommunicationFailureException {
        frontEndRoutingSupportBridgeService.updateRoute(routeID, frontEndRouteUpdateRequestModel);
    }

    @Override
    public boolean processStatusChange(Long routeID) throws CommunicationFailureException {
        return frontEndRoutingSupportBridgeService.changeStatus(routeID).enabled();
    }

    @Override
    public void processDeleteRoute(Long routeID) throws CommunicationFailureException {
        frontEndRoutingSupportBridgeService.deleteRoute(routeID);
    }
}
