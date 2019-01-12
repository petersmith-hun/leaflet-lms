package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.system.SEOConfiguration;
import hu.psprog.leaflet.lms.service.facade.SystemConfigurationFacade;
import hu.psprog.leaflet.lms.web.controller.pagination.LogViewerPaginationHelper;
import hu.psprog.leaflet.tlp.api.domain.LogEventPage;
import hu.psprog.leaflet.tlp.api.domain.LogRequest;
import hu.psprog.leaflet.tlp.api.domain.OrderBy;
import hu.psprog.leaflet.tlp.api.domain.OrderDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * System configuration management controller for LMS.
 *
 * @author Peter Smith
 */
@Controller
@RequestMapping(SystemConfigurationController.PATH_SYSTEM)
public class SystemConfigurationController extends BaseController {

    private static final String VIEW_SYSTEM_SEO_EDITOR_FORM = "view/system/seo_editor_form";
    private static final String VIEW_SYSTEM_FAILOVER = "view/system/failover";
    private static final String VIEW_SYSTEM_LOGS = "view/system/logs";

    private static final String PATH_SEO = "/seo";
    private static final String PATH_FAILOVER = "/failover";
    private static final String PATH_LOGS = "/logs";

    static final String PATH_SYSTEM = "/system";
    private static final String PATH_EDIT_SEO = PATH_SYSTEM + PATH_SEO;

    private SystemConfigurationFacade systemConfigurationFacade;
    private LogViewerPaginationHelper logViewerPaginationHelper;

    @Autowired
    public SystemConfigurationController(SystemConfigurationFacade systemConfigurationFacade, LogViewerPaginationHelper logViewerPaginationHelper) {
        this.systemConfigurationFacade = systemConfigurationFacade;
        this.logViewerPaginationHelper = logViewerPaginationHelper;
    }

    /**
     * Renders SEO configuration form.
     *
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_SEO)
    public ModelAndView showSEOConfigurationForm() throws CommunicationFailureException {

        return modelAndViewFactory
                .createForView(VIEW_SYSTEM_SEO_EDITOR_FORM)
                .withAttribute("seo", systemConfigurationFacade.getCurrentSEOConfiguration())
                .build();
    }

    /**
     * Processes SEO configuration change request.
     *
     * @param seoConfiguration update SEO values
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object (redirection to the SEO configuration form)
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_SEO)
    public ModelAndView processSEOConfigurationChange(@ModelAttribute SEOConfiguration seoConfiguration, RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        return handleValidationFailure(() -> {
            systemConfigurationFacade.processUpdateSEOConfiguration(seoConfiguration);
            return modelAndViewFactory.createRedirectionTo(PATH_EDIT_SEO);
        }, validationFailureRedirectionSupplier(redirectAttributes, seoConfiguration, PATH_EDIT_SEO));
    }

    /**
     * Renders failover status page.
     *
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on communication failure with CBFS
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_FAILOVER)
    public ModelAndView showFailoverStatus() throws CommunicationFailureException {

        return modelAndViewFactory.createForView(VIEW_SYSTEM_FAILOVER)
                .withAttribute("status", systemConfigurationFacade.getFailoverStatus())
                .build();
    }

    /**
     * Shows log filter form.
     * If a query has already been sent via the form, applicable logs will be retrieved and listed.
     *
     * @param logRequest paging and filtering parameters (from query parameters) collected as {@link LogRequest} object
     * @param request {@link HttpServletRequest} object
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on communication failure with TLP
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_LOGS)
    public ModelAndView showRetrievedLogs(LogRequest logRequest, HttpServletRequest request) throws CommunicationFailureException {

        return modelAndViewFactory.createForView(VIEW_SYSTEM_LOGS)
                .withAttribute("orderByOptions", OrderBy.values())
                .withAttribute("orderDirectionOptions", OrderDirection.values())
                .withAttribute("logs", getLogs(logRequest))
                .withAttribute("pagination", logViewerPaginationHelper.extractPaginationAttributes(request))
                .build();
    }

    private LogEventPage getLogs(LogRequest logRequest) throws CommunicationFailureException {
        return logRequest.isQueried()
                ? systemConfigurationFacade.getLogs(logRequest)
                : null;
    }
}
