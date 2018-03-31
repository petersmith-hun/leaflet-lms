package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.lms.service.domain.translations.TranslationPackUploadRequestModel;
import hu.psprog.leaflet.lms.service.facade.TranslationManagementFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/**
 * Translation (internationalization) management controller for LMS.
 *
 * @author Peter Smith
 */
@Controller
@RequestMapping(TranslationManagementController.PATH_SYSTEM_TRANSLATIONS)
public class TranslationManagementController extends BaseController {

    private static final String VIEW_TRANSLATIONS_LIST = "view/translations/list";
    private static final String VIEW_TRANSLATIONS_DETAILS = "view/translations/details";
    private static final String VIEW_TRANSLATIONS_EDIT_FORM = "view/translations/edit_form";

    private static final String TRANSLATION_PACK_SUCCESSFULLY_CREATED = "Translation pack successfully created";
    private static final String TRANSLATION_PACK_STATUS_SUCCESSFULLY_CHANGED = "Translation pack status successfully changed to %s";
    private static final String TRANSLATION_PACK_SUCCESSFULLY_DELETED = "Translation pack successfully deleted";

    static final String PATH_SYSTEM_TRANSLATIONS = "/system/translations";

    private TranslationManagementFacade translationManagementFacade;

    @Autowired
    public TranslationManagementController(TranslationManagementFacade translationManagementFacade) {
        this.translationManagementFacade = translationManagementFacade;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView listPacks() {

        return modelAndViewFactory.createForView(VIEW_TRANSLATIONS_LIST)
                .withAttribute("packs", translationManagementFacade.getPacks())
                .build();
    }

    @RequestMapping(method = RequestMethod.GET, path = PATH_VIEW)
    public ModelAndView viewPackDetails(@PathVariable(PATH_VARIABLE_ID) UUID packID) {

        return modelAndViewFactory.createForView(VIEW_TRANSLATIONS_DETAILS)
                .withAttribute("pack", translationManagementFacade.getPack(packID))
                .build();
    }

    @RequestMapping(method = RequestMethod.GET, path = PATH_CREATE)
    public ModelAndView showCreatePackForm() {

        return modelAndViewFactory.createForView(VIEW_TRANSLATIONS_EDIT_FORM)
                .build();
    }

    @RequestMapping(method = RequestMethod.POST, path = PATH_CREATE)
    public ModelAndView processPackCreation(TranslationPackUploadRequestModel translationPackUploadRequestModel, RedirectAttributes redirectAttributes) {

        UUID packID = translationManagementFacade.processCreatePack(translationPackUploadRequestModel);
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, TRANSLATION_PACK_SUCCESSFULLY_CREATED);

        return modelAndViewFactory.createRedirectionTo(getRedirectionPath(packID));
    }

    @RequestMapping(method = RequestMethod.POST, path = PATH_STATUS)
    public ModelAndView processStatusChange(@PathVariable(PATH_VARIABLE_ID) UUID packID, RedirectAttributes redirectAttributes) {

        String status = translationManagementFacade.processChangePackStatus(packID)
                ? "enabled"
                : "disabled";
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, String.format(TRANSLATION_PACK_STATUS_SUCCESSFULLY_CHANGED, status));

        return modelAndViewFactory.createRedirectionTo(getRedirectionPath(packID));
    }

    @RequestMapping(method = RequestMethod.POST, path = PATH_DELETE)
    public ModelAndView processPackDeletion(@PathVariable(PATH_VARIABLE_ID) UUID packID, RedirectAttributes redirectAttributes) {

        translationManagementFacade.processDeletePack(packID);
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, TRANSLATION_PACK_SUCCESSFULLY_DELETED);

        return modelAndViewFactory.createRedirectionTo(PATH_SYSTEM_TRANSLATIONS);
    }

    private String getRedirectionPath(UUID packID) {
        return PATH_SYSTEM_TRANSLATIONS + replaceIDInViewPath(packID);
    }
}
