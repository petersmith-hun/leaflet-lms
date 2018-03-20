package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.routing.FrontEndRouteUpdateRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.facade.FrontEndRoutingSupportFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Front-end routing support management controller for LMS.
 *
 * @author Peter Smith
 */
@Controller
@RequestMapping(FrontEndRoutingManagementController.PATH_SYSTEM_ROUTES)
public class FrontEndRoutingManagementController extends BaseController {

    private static final String VIEW_ROUTES_LIST = "view/routes/list";
    private static final String VIEW_ROUTES_DETAILS = "view/routes/details";
    private static final String VIEW_ROUTES_EDIT_FORM = "view/routes/edit_form";

    private static final String ROUTE_SUCCESSFULLY_SAVED = "Route successfully saved";
    private static final String ROUTE_SUCCESSFULLY_UPDATED = "Route successfully updated";
    private static final String ROUTE_STATUS_SUCCESSFULLY_CHANGED = "Route status successfully changed to %s";
    private static final String ROUTE_SUCCESSFULLY_DELETED = "Route successfully deleted";

    static final String PATH_SYSTEM_ROUTES = "/system/routes";

    private FrontEndRoutingSupportFacade frontEndRoutingSupportFacade;

    @Autowired
    public FrontEndRoutingManagementController(FrontEndRoutingSupportFacade frontEndRoutingSupportFacade) {
        this.frontEndRoutingSupportFacade = frontEndRoutingSupportFacade;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView listRoutes() throws CommunicationFailureException {

        return modelAndViewFactory.createForView(VIEW_ROUTES_LIST)
                .withAttribute("routes", frontEndRoutingSupportFacade.getRoutes())
                .build();
    }

    @RequestMapping(method = RequestMethod.GET, path = PATH_VIEW)
    public ModelAndView viewRoute(@PathVariable(PATH_VARIABLE_ID) Long routeID) throws CommunicationFailureException {

        return modelAndViewFactory.createForView(VIEW_ROUTES_DETAILS)
                .withAttribute("route", frontEndRoutingSupportFacade.getRoute(routeID))
                .build();
    }

    @RequestMapping(method = RequestMethod.GET, path = PATH_CREATE)
    public ModelAndView showCreateRouteForm() {

        return modelAndViewFactory.createForView(VIEW_ROUTES_EDIT_FORM)
                .build();
    }

    @RequestMapping(method = RequestMethod.POST, path = PATH_CREATE)
    public ModelAndView processRouteCreation(@ModelAttribute FrontEndRouteUpdateRequestModel frontEndRouteUpdateRequestModel,
                                             RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        Long createdID = frontEndRoutingSupportFacade.processCreateRoute(frontEndRouteUpdateRequestModel);
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, ROUTE_SUCCESSFULLY_SAVED);

        return modelAndViewFactory.createRedirectionTo(getRedirectionPath(createdID));
    }

    @RequestMapping(method = RequestMethod.GET, path = PATH_EDIT)
    public ModelAndView showEditRouteForm(@PathVariable(PATH_VARIABLE_ID) Long routeID) throws CommunicationFailureException {

        return modelAndViewFactory.createForView(VIEW_ROUTES_EDIT_FORM)
                .withAttribute("route", frontEndRoutingSupportFacade.getRoute(routeID))
                .build();
    }

    @RequestMapping(method = RequestMethod.POST, path = PATH_EDIT)
    public ModelAndView processRouteEditing(@PathVariable(PATH_VARIABLE_ID) Long routeID,
                                            @ModelAttribute FrontEndRouteUpdateRequestModel frontEndRouteUpdateRequestModel,
                                            RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        frontEndRoutingSupportFacade.processEditRoute(routeID, frontEndRouteUpdateRequestModel);
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, ROUTE_SUCCESSFULLY_UPDATED);

        return modelAndViewFactory.createRedirectionTo(getRedirectionPath(routeID));
    }

    @RequestMapping(method = RequestMethod.POST, path = PATH_STATUS)
    public ModelAndView processRouteStatusChange(@PathVariable(PATH_VARIABLE_ID) Long routeID, RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        String currentStatus = frontEndRoutingSupportFacade.processStatusChange(routeID)
                ? "enabled"
                : "disabled";
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, String.format(ROUTE_STATUS_SUCCESSFULLY_CHANGED, currentStatus));

        return modelAndViewFactory.createRedirectionTo(getRedirectionPath(routeID));
    }

    @RequestMapping(method = RequestMethod.POST, path = PATH_DELETE)
    public ModelAndView processRouteDeletion(@PathVariable(PATH_VARIABLE_ID) Long routeID, RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        frontEndRoutingSupportFacade.processDeleteRoute(routeID);
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, ROUTE_SUCCESSFULLY_DELETED);

        return modelAndViewFactory.createRedirectionTo(PATH_SYSTEM_ROUTES);
    }

    private String getRedirectionPath(Long routeID) {
        return PATH_SYSTEM_ROUTES + replaceIDInViewPath(routeID);
    }
}
