package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.lms.service.facade.DashboardFacade;
import hu.psprog.leaflet.lms.web.factory.ModelAndViewFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Dashboard controller.
 * Renders various status information about the system.
 *
 * @author Peter Smith
 */
@Controller
@RequestMapping("/")
public class DashboardController {

    private static final String VIEW_DASHBOARD_HOME = "view/dashboard/home";

    private final DashboardFacade dashboardFacade;
    private final ModelAndViewFactory modelAndViewFactory;

    @Autowired
    public DashboardController(DashboardFacade dashboardFacade, ModelAndViewFactory modelAndViewFactory) {
        this.dashboardFacade = dashboardFacade;
        this.modelAndViewFactory = modelAndViewFactory;
    }

    /**
     * Renders dashboard home page.
     *
     * @return populated {@link ModelAndView} object
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView home() {

        return modelAndViewFactory.createForView(VIEW_DASHBOARD_HOME)
                .withAttribute("registeredServices", dashboardFacade.getRegisteredServices())
                .withAttribute("stackAdminClient", dashboardFacade.getJSClientHelperModel())
                .build();
    }
}
