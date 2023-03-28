package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.facade.CommentFacade;
import hu.psprog.leaflet.lms.service.facade.DashboardFacade;
import hu.psprog.leaflet.lms.web.controller.pagination.CommentPaginationHelper;
import hu.psprog.leaflet.lms.web.factory.ModelAndViewFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

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
    private static final int COMMENT_PAGE_SIZE = 3;

    private final CommentFacade commentFacade;
    private final DashboardFacade dashboardFacade;
    private final CommentPaginationHelper paginationHelper;
    private final ModelAndViewFactory modelAndViewFactory;

    @Autowired
    public DashboardController(CommentFacade commentFacade, DashboardFacade dashboardFacade,
                               CommentPaginationHelper paginationHelper, ModelAndViewFactory modelAndViewFactory) {
        this.commentFacade = commentFacade;
        this.dashboardFacade = dashboardFacade;
        this.paginationHelper = paginationHelper;
        this.modelAndViewFactory = modelAndViewFactory;
    }

    /**
     * Renders dashboard home page.
     *
     * @return populated {@link ModelAndView} object
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView home(@RequestParam(value = "commentPage", required = false, defaultValue = "1") int commentPage,
                             HttpServletRequest request) throws CommunicationFailureException {

        var pendingComments = commentFacade.getPendingComments(commentPage, COMMENT_PAGE_SIZE);

        return modelAndViewFactory.createForView(VIEW_DASHBOARD_HOME)
                .withAttribute("pendingComments", pendingComments)
                .withAttribute("pagination", paginationHelper.extractPaginationAttributes(pendingComments, request))
                .withAttribute("registeredServices", dashboardFacade.getRegisteredServices())
                .withAttribute("stackAdminClient", dashboardFacade.getJSClientHelperModel())
                .build();
    }
}
