package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.entry.EntryInitialStatus;
import hu.psprog.leaflet.api.rest.request.entry.EntrySearchParameters;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EditEntryDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EntrySearchResultDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.entry.EntryFormContent;
import hu.psprog.leaflet.lms.service.domain.entry.ModifyEntryRequest;
import hu.psprog.leaflet.lms.service.facade.CategoryFacade;
import hu.psprog.leaflet.lms.service.facade.CommentFacade;
import hu.psprog.leaflet.lms.service.facade.EntryFacade;
import hu.psprog.leaflet.lms.web.controller.pagination.EntryPaginationHelper;
import hu.psprog.leaflet.lms.web.factory.ModelAndViewFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

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
    private static final String ENTRY_PUBLICATION_STATUS_SUCCESSFULLY_CHANGED = "Entry publication status successfully changed to %s";
    private static final String ENTRY_SUCCESSFULLY_DELETED = "Entry successfully deleted.";

    static final String PATH_ENTRIES = "/entries";
    private static final String PATH_CREATE_ENTRY = PATH_ENTRIES + PATH_CREATE;

    private final EntryFacade entryFacade;
    private final CategoryFacade categoryFacade;
    private final CommentFacade commentFacade;
    private final EntryPaginationHelper paginationHelper;
    private final String resourceServerUrl;

    @Autowired
    public EntriesController(ModelAndViewFactory modelAndViewFactory, EntryFacade entryFacade,
                             CategoryFacade categoryFacade, CommentFacade commentFacade,
                             EntryPaginationHelper paginationHelper,
                             @Value("${webapp.resource-server-url}") String resourceServerUrl) {
        super(modelAndViewFactory);
        this.entryFacade = entryFacade;
        this.categoryFacade = categoryFacade;
        this.commentFacade = commentFacade;
        this.paginationHelper = paginationHelper;
        this.resourceServerUrl = resourceServerUrl;
    }

    /**
     * Returns entries for management interface.
     *
     * @param entrySearchParameters search parameters
     * @param request {@link HttpServletRequest} object
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = {PATH_CURRENT, PATH_OPTIONAL_PAGE_NUMBER})
    public ModelAndView listEntries(EntrySearchParameters entrySearchParameters,
                                    HttpServletRequest request)
            throws CommunicationFailureException {

        WrapperBodyDataModel<EntrySearchResultDataModel> response = entryFacade.getEntries(entrySearchParameters);
        Map<Long, Long> pendingCommentsCountMap = commentFacade.getNumberOfPendingCommentsByEntry();

        return modelAndViewFactory.createForView(VIEW_ENTRIES_LIST)
                .withAttribute("content", response.body())
                .withAttribute("pendingComments", pendingCommentsCountMap)
                .withAttribute("categories", categoryFacade.getAllCategories())
                .withAttribute("pagination", paginationHelper.extractPaginationAttributes(response, request))
                .withAttribute("searchParameters", entrySearchParameters)
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
        Long numberOfPendingComments = commentFacade.getNumberOfPendingCommentsForEntry(entryID);

        return modelAndViewFactory.createForView(VIEW_ENTRY_DETAILS)
                .withAttribute("entryData", response)
                .withAttribute("pendingCommentCount", numberOfPendingComments)
                .withAttribute("resourceServerUrl", resourceServerUrl)
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
                .withAttribute("tagSelector", response.existingTags())
                .withAttribute("categorySelector", response.existingCategories())
                .withAttribute("fileSelector", response.existingFiles())
                .withAttribute("resourceServerUrl", resourceServerUrl)
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

        return handleValidationFailure(() -> {
            modifyEntryRequest.setUserID(currentUserID());

            Long createdID = entryFacade.processCreateEntry(modifyEntryRequest);
            String viewPath = PATH_ENTRIES + replaceIDInViewPath(createdID);

            redirectAttributes.addFlashAttribute(FLASH_MESSAGE, ENTRY_SUCCESSFULLY_SAVED);

            return modelAndViewFactory.createRedirectionTo(viewPath);
        }, validationFailureRedirectionSupplier(redirectAttributes, modifyEntryRequest, PATH_CREATE_ENTRY));
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
                .withAttribute("tagSelector", response.existingTags())
                .withAttribute("categorySelector", response.existingCategories())
                .withAttribute("fileSelector", response.existingFiles())
                .withAttribute("entryData", response.entryData())
                .withAttribute("attachedFileReferences", response.attachedFileReferences())
                .withAttribute("resourceServerUrl", resourceServerUrl)
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

        return handleValidationFailure(() -> {
            entryFacade.processEditEntry(id, modifyEntryRequest);

            redirectAttributes.addFlashAttribute(FLASH_MESSAGE, ENTRY_SUCCESSFULLY_SAVED);

            return modelAndViewFactory.createRedirectionTo(getRedirectionPath(id));
        }, validationFailureRedirectionSupplier(redirectAttributes, modifyEntryRequest, getRedirectionPath(id)));
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
     * Processes an entry publication status change request.
     *
     * @param id ID of the entry to change publication status of
     * @param status new publication status
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object - redirecting to the view entry page
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_PUBLICATION)
    public ModelAndView processPublicationTransition(@PathVariable(PATH_VARIABLE_ID) Long id, @RequestParam("status") EntryInitialStatus status,
                                                     RedirectAttributes redirectAttributes) throws CommunicationFailureException {

        entryFacade.processPublicationStatusTransition(id, status);
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, String.format(ENTRY_PUBLICATION_STATUS_SUCCESSFULLY_CHANGED, status.name().toLowerCase()));

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
