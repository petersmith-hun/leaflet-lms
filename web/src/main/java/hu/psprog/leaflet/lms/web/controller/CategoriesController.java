package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.category.CategoryCreateRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.facade.CategoryFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Categories management controller.
 *
 * @author Peter Smith
 */
@Controller
@RequestMapping(CategoriesController.PATH_CATEGORIES)
public class CategoriesController extends BaseController {

    private static final String VIEW_CATEGORIES_DETAILS = "view/categories/details";
    private static final String VIEW_CATEGORIES_LIST = "view/categories/list";
    private static final String VIEW_CATEGORIES_EDIT_FORM = "view/categories/edit_form";

    private static final String CATEGORY_SUCCESSFULLY_CREATED = "Category successfully created.";
    private static final String CATEGORY_SUCCESSFULLY_UPDATED = "Category successfully updated.";
    private static final String CATEGORY_SUCCESSFULLY_DELETED = "Category successfully deleted.";
    private static final String CATEGORY_STATUS_SUCCESSFULLY_CHANGED = "Category status successfully changed to %s";

    static final String PATH_CATEGORIES = "/categories";
    private static final String PATH_CREATE_CATEGORY = PATH_CATEGORIES + PATH_CREATE;

    private CategoryFacade categoryFacade;

    @Autowired
    public CategoriesController(CategoryFacade categoryFacade) {
        this.categoryFacade = categoryFacade;
    }

    /**
     * Returns categories for management interface.
     *
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView listCategories() throws CommunicationFailureException {

        return modelAndViewFactory
                .createForView(VIEW_CATEGORIES_LIST)
                .withAttribute("categories", categoryFacade.getAllCategories())
                .build();
    }

    /**
     * Returns category identified by given ID to view its details page.
     *
     * @param categoryID ID of the category to view details of
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_VIEW)
    public ModelAndView viewCategory(@PathVariable(PATH_VARIABLE_ID) Long categoryID) throws CommunicationFailureException {

        return modelAndViewFactory
                .createForView(VIEW_CATEGORIES_DETAILS)
                .withAttribute("category", categoryFacade.getCategory(categoryID))
                .build();
    }

    /**
     * Renders category creation form.
     *
     * @return populated {@link ModelAndView} object
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_CREATE)
    public ModelAndView showCategoryCreateForm() {

        return modelAndViewFactory
                .createForView(VIEW_CATEGORIES_EDIT_FORM)
                .build();
    }

    /**
     * Processes category creation request.
     *
     * @param categoryCreateRequestModel category data
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object (redirection to view the created category)
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_CREATE)
    public ModelAndView processCreateCategory(@ModelAttribute CategoryCreateRequestModel categoryCreateRequestModel, RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        return handleValidationFailure(() -> {
            Long createdCategoryID = categoryFacade.processCreateCategory(categoryCreateRequestModel);
            redirectAttributes.addFlashAttribute(FLASH_MESSAGE, CATEGORY_SUCCESSFULLY_CREATED);

            return modelAndViewFactory.createRedirectionTo(getRedirectionPath(createdCategoryID));
        }, validationFailureRedirectionSupplier(redirectAttributes, categoryCreateRequestModel, PATH_CREATE_CATEGORY));
    }

    /**
     * Renders category edit form.
     *
     * @param categoryID ID of the category to edit
     * @return populated {@link ModelAndView} object
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.GET, path = PATH_EDIT)
    public ModelAndView showCategoryEditForm(@PathVariable(PATH_VARIABLE_ID) Long categoryID) throws CommunicationFailureException {

        return modelAndViewFactory
                .createForView(VIEW_CATEGORIES_EDIT_FORM)
                .withAttribute("category", categoryFacade.getCategory(categoryID))
                .build();
    }

    /**
     * Processes category edit request.
     *
     * @param categoryID ID of the category to edit
     * @param categoryCreateRequestModel updated category data
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object (redirection to view the edited category)
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_EDIT)
    public ModelAndView processCategoryEdit(@PathVariable(PATH_VARIABLE_ID) Long categoryID,
                                            @ModelAttribute CategoryCreateRequestModel categoryCreateRequestModel,
                                            RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        return handleValidationFailure(() -> {
            categoryFacade.processEditCategory(categoryID, categoryCreateRequestModel);
            redirectAttributes.addFlashAttribute(FLASH_MESSAGE, CATEGORY_SUCCESSFULLY_UPDATED);

            return modelAndViewFactory.createRedirectionTo(getRedirectionPath(categoryID));
        }, validationFailureRedirectionSupplier(redirectAttributes, categoryCreateRequestModel, getRedirectionPath(categoryID)));
    }

    /**
     * Processes category deletion request.
     *
     * @param categoryID ID of the category to delete
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object (redirection to the category list)
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_DELETE)
    public ModelAndView processDeleteCategory(@PathVariable(PATH_VARIABLE_ID) Long categoryID, RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        categoryFacade.processDeleteCategory(categoryID);
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, CATEGORY_SUCCESSFULLY_DELETED);

        return modelAndViewFactory.createRedirectionTo(PATH_CATEGORIES);
    }

    /**
     * Processes category status change request.
     *
     * @param categoryID ID of the category to change status of
     * @param redirectAttributes redirection attributes
     * @return populated {@link ModelAndView} object (redirection to view the changed category)
     * @throws CommunicationFailureException on Bridge communication failure
     */
    @RequestMapping(method = RequestMethod.POST, path = PATH_STATUS)
    public ModelAndView processChangeCategoryStatus(@PathVariable(PATH_VARIABLE_ID) Long categoryID, RedirectAttributes redirectAttributes)
            throws CommunicationFailureException {

        String currentStatus = categoryFacade.processChangeStatus(categoryID)
                ? "enabled"
                : "disabled";
        redirectAttributes.addFlashAttribute(FLASH_MESSAGE, String.format(CATEGORY_STATUS_SUCCESSFULLY_CHANGED, currentStatus));

        return modelAndViewFactory.createRedirectionTo(getRedirectionPath(categoryID));
    }

    private String getRedirectionPath(Long categoryID) {
        return PATH_CATEGORIES + replaceIDInViewPath(categoryID);
    }
}
