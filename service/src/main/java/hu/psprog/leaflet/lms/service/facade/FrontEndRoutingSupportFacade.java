package hu.psprog.leaflet.lms.service.facade;

import hu.psprog.leaflet.api.rest.request.routing.FrontEndRouteUpdateRequestModel;
import hu.psprog.leaflet.api.rest.response.routing.ExtendedFrontEndRouteDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;

import java.util.List;

/**
 * Front-end routing support operations facade.
 *
 * @author Peter Smith
 */
public interface FrontEndRoutingSupportFacade {


    /**
     * Returns all existing route items.
     *
     * @return list of existing route items
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    List<ExtendedFrontEndRouteDataModel> getRoutes() throws CommunicationFailureException;

    /**
     * Returns route item identified by given ID.
     *
     * @param routeID ID of the route item
     * @return identified route item
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    ExtendedFrontEndRouteDataModel getRoute(Long routeID) throws CommunicationFailureException;

    /**
     * Processes route creation request.
     *
     * @param frontEndRouteUpdateRequestModel {@link FrontEndRouteUpdateRequestModel} containing route information
     * @return ID of created route
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    Long processCreateRoute(FrontEndRouteUpdateRequestModel frontEndRouteUpdateRequestModel) throws CommunicationFailureException;

    /**
     * Processes route editing request.
     *
     * @param routeID ID of the route item
     * @param frontEndRouteUpdateRequestModel {@link FrontEndRouteUpdateRequestModel} containing updated route information
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void processEditRoute(Long routeID, FrontEndRouteUpdateRequestModel frontEndRouteUpdateRequestModel) throws CommunicationFailureException;

    /**
     * Processes route status change (enabled/disabled) request.
     *
     * @param routeID ID of the route item
     * @return current status as boolean, {@code true} if enabled, {@code false} otherwise
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    boolean processStatusChange(Long routeID) throws CommunicationFailureException;

    /**
     * Processes route deletion request.
     *
     * @param routeID ID of the route item
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void processDeleteRoute(Long routeID) throws CommunicationFailureException;
}
