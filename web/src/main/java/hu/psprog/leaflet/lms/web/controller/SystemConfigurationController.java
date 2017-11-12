package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.system.SEOConfiguration;
import hu.psprog.leaflet.lms.service.facade.SystemConfigurationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * System configuration management controller for LMS.
 *
 * @author Peter Smith
 */
@Controller
@RequestMapping(SystemConfigurationController.PATH_SYSTEM)
public class SystemConfigurationController extends BaseController {

    private static final String VIEW_SYSTEM_SEO_EDITOR_FORM = "view/system/seo_editor_form";

    private static final String PATH_SEO = "/seo";

    static final String PATH_SYSTEM = "/system";

    private SystemConfigurationFacade systemConfigurationFacade;

    @Autowired
    public SystemConfigurationController(SystemConfigurationFacade systemConfigurationFacade) {
        this.systemConfigurationFacade = systemConfigurationFacade;
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
     * @return populated {@link ModelAndView} object (redirection to the SEO configuration form)
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_SEO)
    public ModelAndView processSEOConfigurationChange(@ModelAttribute SEOConfiguration seoConfiguration) throws CommunicationFailureException {

        systemConfigurationFacade.processUpdateSEOConfiguration(seoConfiguration);

        return modelAndViewFactory
                .createRedirectionTo(PATH_SYSTEM + PATH_SEO);
    }
}
