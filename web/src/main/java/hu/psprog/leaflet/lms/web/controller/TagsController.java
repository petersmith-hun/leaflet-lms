package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.tag.TagCreateRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.facade.TagFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Tag management controller for LMS.
 *
 * @author Peter Smith
 */
@Controller
@RequestMapping(TagsController.PATH_TAGS)
public class TagsController extends BaseController {

    private static final String VIEW_TAGS_LIST = "view/tags/list";
    private static final String VIEW_TAGS_EDIT_FORM = "view/tags/edit_form";
    private static final String VIEW_TAGS_DETAILS = "view/tags/details";

    private static final String TAG_SUCCESSFULLY_CREATED = "Tag successfully created.";
    private static final String TAG_SUCCESSFULLY_UPDATED = "Tag successfully updated.";
    private static final String TAG_SUCCESSFULLY_DELETED = "Tag successfully deleted.";
    private static final String TAG_STATUS_SUCCESSFULLY_UPDATED = "Tag status successfully updated to %s";

    static final String PATH_TAGS = "/tags";
    private static final String PATH_CREATE_TAG = PATH_TAGS + PATH_CREATE;

    private TagFacade tagFacade;

    @Autowired
    public TagsController(TagFacade tagFacade) {
        this.tagFacade = tagFacade;
    }

    /**
     * Returns list of existing tags.
     *
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView listTags() throws CommunicationFailureException {

        return modelAndViewFactory
                .createForView(VIEW_TAGS_LIST)
                .withAttribute("tags", tagFacade.getAllTags())
                .build();
    }

    /**
     * Returns tag identified by given ID.
     *
     * @param tagID ID of the tag to retrieve
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_VIEW)
    public ModelAndView viewTag(@PathVariable(PATH_VARIABLE_ID) Long tagID) throws CommunicationFailureException {

        return modelAndViewFactory
                .createForView(VIEW_TAGS_DETAILS)
                .withAttribute("tag", tagFacade.getTag(tagID))
                .build();
    }

    /**
     * Renders tag creation form.
     *
     * @return populated {@link ModelAndView} object
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_CREATE)
    public ModelAndView showCreateTagForm() {

        return modelAndViewFactory
                .createForView(VIEW_TAGS_EDIT_FORM)
                .build();
    }

    /**
     * Processes tag creation request.
     *
     * @param tagCreateRequestModel tag data
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object (redirection to created tag)
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_CREATE)
    public ModelAndView processCreateTag(@ModelAttribute TagCreateRequestModel tagCreateRequestModel,
                                         RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        return handleValidationFailure(() -> {
            Long createdID = tagFacade.processCreateTag(tagCreateRequestModel);
            redirectAttributes.addFlashAttribute(FLASH_MESSAGE, TAG_SUCCESSFULLY_CREATED);

            return modelAndViewFactory.createRedirectionTo(getRedirectionPath(createdID));
        }, validationFailureRedirectionSupplier(redirectAttributes, tagCreateRequestModel, PATH_CREATE_TAG));
    }

    /**
     * Renders tag edit form.
     *
     * @param tagID ID of the tag to edit
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_EDIT)
    public ModelAndView showEditTagForm(@PathVariable(PATH_VARIABLE_ID) Long tagID) throws CommunicationFailureException {

        return modelAndViewFactory
                .createForView(VIEW_TAGS_EDIT_FORM)
                .withAttribute("tag", tagFacade.getTag(tagID))
                .build();
    }

    /**
     * Processes tag edit request.
     *
     * @param tagID ID of the tag to edit
     * @param tagCreateRequestModel update tag data
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object (redirection to edited tag)
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_EDIT)
    public ModelAndView processEditTag(@PathVariable(PATH_VARIABLE_ID) Long tagID,
                                       @ModelAttribute TagCreateRequestModel tagCreateRequestModel,
                                       RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        return handleValidationFailure(() -> {
            tagFacade.processEditTag(tagID, tagCreateRequestModel);
            redirectAttributes.addFlashAttribute(FLASH_MESSAGE, TAG_SUCCESSFULLY_UPDATED);

            return modelAndViewFactory.createRedirectionTo(getRedirectionPath(tagID));
        }, validationFailureRedirectionSupplier(redirectAttributes, tagCreateRequestModel, getRedirectionPath(tagID)));
    }

    /**
     * Processes tag deletion request.
     *
     * @param tagID ID of the tag to delete
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object (redirection to tag list)
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_DELETE)
    public ModelAndView processDeleteTag(@PathVariable(PATH_VARIABLE_ID) Long tagID, RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        tagFacade.processDeleteTag(tagID);
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, TAG_SUCCESSFULLY_DELETED);

        return modelAndViewFactory.createRedirectionTo(PATH_TAGS);
    }

    /**
     * Processes tag status change request.
     *
     * @param tagID ID of the tag to update status of
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object (redirection to edited tag)
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_STATUS)
    public ModelAndView processStatusChange(@PathVariable(PATH_VARIABLE_ID) Long tagID, RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        String currentStatus = tagFacade.processStatusChange(tagID)
                ? "enabled"
                : "disabled";
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, String.format(TAG_STATUS_SUCCESSFULLY_UPDATED, currentStatus));

        return modelAndViewFactory.createRedirectionTo(getRedirectionPath(tagID));
    }

    private String getRedirectionPath(Long tagID) {
        return PATH_TAGS + replaceIDInViewPath(tagID);
    }
}
