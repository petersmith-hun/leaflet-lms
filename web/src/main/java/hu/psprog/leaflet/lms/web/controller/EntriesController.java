package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EditEntryDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EntryListDataModel;
import hu.psprog.leaflet.bridge.client.domain.OrderBy;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.entry.EntryFormContent;
import hu.psprog.leaflet.lms.service.domain.entry.ModifyEntryRequest;
import hu.psprog.leaflet.lms.service.facade.EntryFacade;
import hu.psprog.leaflet.lms.web.controller.pagination.EntryPaginationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static hu.psprog.leaflet.lms.web.controller.pagination.PaginationHelper.PARAMETER_LIMIT;
import static hu.psprog.leaflet.lms.web.controller.pagination.PaginationHelper.PARAMETER_ORDER_BY;
import static hu.psprog.leaflet.lms.web.controller.pagination.PaginationHelper.PARAMETER_ORDER_DIRECTION;
import static hu.psprog.leaflet.lms.web.controller.pagination.PaginationHelper.PARAMETER_PAGE;

/**
 * Entry management controller for LMS.
 *
 * @author Peter Smith
 */
@Controller
@RequestMapping(EntriesController.PATH_ENTRIES)
public class EntriesController extends BaseController {

    private static final String VIEW_ENTRIES_LIST = "view/entries/list";
    private static final String VIEW_ENTRIES_EDIT_FORM = "view/entries/edit_form";
    private static final String VIEW_ENTRY_DETAILS = "view/entries/details";

    private static final String ENTRY_SUCCESSFULLY_SAVED = "Entry successfully saved.";
    private static final String ENTRY_STATUS_SUCCESSFULLY_CHANGED = "Entry status successfully changed to %s";
    private static final String ENTRY_SUCCESSFULLY_DELETED = "Entry successfully deleted.";

    static final String PATH_ENTRIES = "/entries";

    private EntryFacade entryFacade;
    private EntryPaginationHelper paginationHelper;

    @Autowired
    public EntriesController(EntryFacade entryFacade, EntryPaginationHelper paginationHelper) {
        this.entryFacade = entryFacade;
        this.paginationHelper = paginationHelper;
    }

    /**
     * Returns entries for management interface.
     *
     * @param page page number (page indexing starts at 1)
     * @param limit number of entries on one page
     * @param orderBy order by {@link OrderBy.Entry} options
     * @param orderDirection order direction (ASC|DESC)
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = {PATH_CURRENT, PATH_OPTIONAL_PAGE_NUMBER})
    public ModelAndView listEntries(@PathVariable(value = PARAMETER_PAGE, required = false) Optional<Integer> page,
                                    @RequestParam(value = PARAMETER_LIMIT, required = false) Optional<Integer> limit,
                                    @RequestParam(value = PARAMETER_ORDER_BY, required = false) Optional<String> orderBy,
                                    @RequestParam(value = PARAMETER_ORDER_DIRECTION, required = false) Optional<String> orderDirection)
            throws CommunicationFailureException {

        WrapperBodyDataModel<EntryListDataModel> response = entryFacade.getEntries(paginationHelper.extractPage(page),
                paginationHelper.getLimit(limit), paginationHelper.mapOrderBy(orderBy), paginationHelper.mapOrderDirection(orderDirection));

        return modelAndViewFactory.createForView(VIEW_ENTRIES_LIST)
                .withAttribute("content", response.getBody())
                .withAttribute("pagination", response.getPagination())
                .build();
    }

    /**
     * Returns entry identified by given ID.
     *
     * @param entryID ID of the entry
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_VIEW)
    public ModelAndView viewEntry(@PathVariable(PATH_VARIABLE_ID) Long entryID) throws CommunicationFailureException {

        WrapperBodyDataModel<EditEntryDataModel> response = entryFacade.getEntry(entryID);

        return modelAndViewFactory.createForView(VIEW_ENTRY_DETAILS)
                .withAttribute("content", response.getBody())
                .withAttribute("seo", response.getSeo())
                .build();
    }

    /**
     * Shows entry creation form.
     *
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_CREATE)
    public ModelAndView showCreateEntryForm() throws CommunicationFailureException {

        EntryFormContent response = entryFacade.fillForm();

        return modelAndViewFactory.createForView(VIEW_ENTRIES_EDIT_FORM)
                .withAttribute("tagSelector", response.getExistingTags())
                .withAttribute("categorySelector", response.getExistingCategories())
                .withAttribute("fileSelector", response.getExistingFiles())
                .build();
    }

    /**
     * Processes posted (by form) entry creation request.
     *
     * @param modifyEntryRequest entry data
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object - redirecting to the view entry page
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_CREATE)
    public ModelAndView processEntryCreation(@ModelAttribute ModifyEntryRequest modifyEntryRequest, RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        modifyEntryRequest.setUserID(currentUserID());

        Long createdID = entryFacade.processCreateEntry(modifyEntryRequest);
        String viewPath = PATH_ENTRIES + replaceIDInViewPath(createdID);

        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, ENTRY_SUCCESSFULLY_SAVED);

        return modelAndViewFactory.createRedirectionTo(viewPath);
    }

    /**
     * Shows entry edit form.
     *
     * @param id ID of the entry to edit
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_EDIT)
    public ModelAndView showEditEntryForm(@PathVariable(PATH_VARIABLE_ID) Long id) throws CommunicationFailureException {

        EntryFormContent response = entryFacade.fillForm(id);

        return modelAndViewFactory.createForView(VIEW_ENTRIES_EDIT_FORM)
                .withAttribute("tagSelector", response.getExistingTags())
                .withAttribute("categorySelector", response.getExistingCategories())
                .withAttribute("fileSelector", response.getExistingFiles())
                .withAttribute("entryData", response.getEntryData())
                .build();
    }

    /**
     * Processes posted (by form) entry editing request.
     *
     * @param id ID of the edited entry
     * @param redirectAttributes redirection attributes
     * @param modifyEntryRequest entry data
     * @return populated {@link ModelAndView} object - redirecting to the view entry page
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_EDIT)
    public ModelAndView processEntryEditing(@PathVariable(PATH_VARIABLE_ID) Long id, @ModelAttribute ModifyEntryRequest modifyEntryRequest, RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        entryFacade.processEditEntry(id, modifyEntryRequest);

        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, ENTRY_SUCCESSFULLY_SAVED);

        return modelAndViewFactory.createRedirectionTo(getRedirectionPath(id));
    }

    /**
     * Processes entry status change request.
     *
     * @param id ID of the entry to change
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object - redirecting to the view entry page
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_STATUS)
    public ModelAndView processStatusChange(@PathVariable(PATH_VARIABLE_ID) Long id, RedirectAttributes redirectAttributes) throws CommunicationFailureException {

        String currentStatus = entryFacade.processStatusChange(id)
                ? "enabled"
                : "disabled";
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, String.format(ENTRY_STATUS_SUCCESSFULLY_CHANGED, currentStatus));

        return modelAndViewFactory.createRedirectionTo(getRedirectionPath(id));
    }

    /**
     * Processes entry deletion request.
     *
     * @param id ID of the entry to delete
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object - redirecting to the view entry page
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_DELETE)
    public ModelAndView processDeletion(@PathVariable(PATH_VARIABLE_ID) Long id, RedirectAttributes redirectAttributes) throws CommunicationFailureException {

        entryFacade.processDeletion(id);
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, ENTRY_SUCCESSFULLY_DELETED);

        return modelAndViewFactory.createRedirectionTo(PATH_ENTRIES);
    }

    private String getRedirectionPath(Long entryID) {
        return PATH_ENTRIES + replaceIDInViewPath(entryID);
    }
}
