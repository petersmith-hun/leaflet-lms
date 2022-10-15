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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

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
    private static final String VIEW_SYSTEM_DOCKER = "view/system/docker";
    private static final String VIEW_SYSTEM_DOCKER_REGISTRY_BROWSER = "view/system/docker_registry_browser";

    private static final String PATH_SEO = "/seo";
    private static final String PATH_FAILOVER = "/failover";
    private static final String PATH_LOGS = "/logs";
    private static final String PATH_DOCKER = "/docker";
    private static final String PATH_DOCKER_REGISTRY = "/docker/registry";

    static final String PATH_SYSTEM = "/system";
    private static final String PATH_EDIT_SEO = PATH_SYSTEM + PATH_SEO;
    private static final String SEO_UPDATED = "Default SEO attributes have been updated";

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
            redirectAttributes.addFlashAttribute(FLASH_MESSAGE, SEO_UPDATED);
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
     * Renders Docker cluster status page.
     *
     * @return populated {@link ModelAndView} object
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_DOCKER)
    public ModelAndView showDockerClusterStatus() {

        return modelAndViewFactory.createForView(VIEW_SYSTEM_DOCKER)
                .withAttribute("existingContainers", systemConfigurationFacade.getExistingContainers())
                .withAttribute("stackAdminClient", systemConfigurationFacade.getJSClientHelperModel())
                .build();
    }

    /**
     * Renders Docker Registry list page.
     *
     * @return populated {@link ModelAndView} object
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_DOCKER_REGISTRY)
    public ModelAndView showConfiguredDockerRegistryList() {

        return modelAndViewFactory.createForView(VIEW_SYSTEM_DOCKER_REGISTRY_BROWSER)
                .withAttribute("registries", systemConfigurationFacade.getConfiguredRegistries())
                .build();
    }

    /**
     * Renders the repository list page for the given Docker Registry.
     *
     * @param registryID ID of the registry to be listed
     * @return populated {@link ModelAndView} object
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_DOCKER_REGISTRY, params = {"registry"})
    public ModelAndView showRepositories(@RequestParam("registry") String registryID) {

        return modelAndViewFactory.createForView(VIEW_SYSTEM_DOCKER_REGISTRY_BROWSER)
                .withAttribute("repositories", systemConfigurationFacade.getDockerRepositories(registryID))
                .withAttribute("currentRegistry", registryID)
                .build();
    }

    /**
     * Renders the tag list page for the given Docker Registry and repository.
     *
     * @param registryID ID of the registry to be listed
     * @param repositoryID ID of the repository to be listed
     * @return populated {@link ModelAndView} object
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_DOCKER_REGISTRY, params = {"registry", "repository"})
    public ModelAndView showRepositoryTags(@RequestParam("registry") String registryID, @RequestParam("repository") String repositoryID) {

        return modelAndViewFactory.createForView(VIEW_SYSTEM_DOCKER_REGISTRY_BROWSER)
                .withAttribute("tags", systemConfigurationFacade.getDockerRepositoryDetails(registryID, repositoryID))
                .withAttribute("currentRegistry", registryID)
                .withAttribute("currentRepository", repositoryID)
                .build();
    }

    /**
     * Processes a Docker image deletion request.
     *
     * @param registryID ID of the registry to be managed
     * @param repositoryID ID of the repository to be managed
     * @param tag tag of the image to be deleted
     * @return populated {@link ModelAndView} object for redirection back to the repository details page
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_DOCKER_REGISTRY, params = {"registry", "repository", "tag"})
    public ModelAndView processImageDeletionByTag(@RequestParam("registry") String registryID, @RequestParam("repository") String repositoryID, @RequestParam("tag") String tag) {

        systemConfigurationFacade.deleteDockerImageByTag(registryID, repositoryID, tag);
        String redirectionPath = String.format("%s%s?registry=%s&repository=%s", PATH_SYSTEM, PATH_DOCKER_REGISTRY, registryID, repositoryID);

        return modelAndViewFactory.createRedirectionTo(redirectionPath);
    }

    /**
     * Shows log filter form for TLP v1 requests.
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

    /**
     * Shows log filter form for TLP v2 requests.
     * If a query has already been sent via the form, applicable logs will be retrieved and listed.
     *
     * @param logRequest TLQL log request string
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on communication failure with TLP
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_LOGS, params = "api=V2")
    public ModelAndView showRetrievedLogs(@RequestParam("query") Optional<String> logRequest) throws CommunicationFailureException {

        return modelAndViewFactory.createForView(VIEW_SYSTEM_LOGS)
                .withAttribute("orderByOptions", OrderBy.values())
                .withAttribute("orderDirectionOptions", OrderDirection.values())
                .withAttribute("logs", getLogs(logRequest))
                .build();
    }

    private LogEventPage getLogs(LogRequest logRequest) throws CommunicationFailureException {
        return logRequest.isQueried()
                ? systemConfigurationFacade.getLogs(logRequest)
                : null;
    }

    private LogEventPage getLogs(Optional<String> logRequest) throws CommunicationFailureException {
        return logRequest.isPresent()
                ? systemConfigurationFacade.getLogs(logRequest.get())
                : null;
    }
}
