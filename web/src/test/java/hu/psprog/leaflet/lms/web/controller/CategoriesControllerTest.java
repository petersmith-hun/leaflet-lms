package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.request.category.CategoryCreateRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import hu.psprog.leaflet.lms.service.facade.CategoryFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link CategoriesController}.
 *
 * @author Peter Smith
 */
@Extensions({
        @ExtendWith(MockitoExtension.class),
        @ExtendWith(SpringExtension.class)
})
public class CategoriesControllerTest extends AbstractControllerTest {

    private static final String CATEGORIES = "categories";
    private static final Long CATEGORY_ID = 2L;
    private static final String CATEGORY_VIEW_PATH = "/categories/view/" + CATEGORY_ID;

    private static final String FIELD_CATEGORY = "category";
    private static final String PATH_CATEGORIES = "/categories";
    private static final String PATH_CATEGORIES_CREATE = PATH_CATEGORIES + "/create";

    @Mock
    private CategoryFacade categoryFacade;

    private CategoriesController categoriesController;

    @BeforeEach
    public void setup() {
        super.setup();
        categoriesController = new CategoriesController(modelAndViewFactory, categoryFacade);
    }

    @Test
    public void shouldListCategories() throws CommunicationFailureException {

        // when
        categoriesController.listCategories();

        // then
        verify(categoryFacade).getAllCategories();
        verifyViewCreated(VIEW_LIST);
        verifyFieldsSet(CATEGORIES);
    }

    @Test
    public void shouldViewCategory() throws CommunicationFailureException {

        // when
        categoriesController.viewCategory(CATEGORY_ID);

        // then
        verify(categoryFacade).getCategory(CATEGORY_ID);
        verifyViewCreated(VIEW_DETAILS);
        verifyFieldsSet(FIELD_CATEGORY);
    }

    @Test
    public void shouldShowCategoryCreateForm() {

        // when
        categoriesController.showCategoryCreateForm();

        // then
        verifyViewCreated(VIEW_EDIT_FORM);
    }

    @Test
    public void shouldProcessCreateCategory() throws CommunicationFailureException {

        // given
        CategoryCreateRequestModel categoryCreateRequestModel = new CategoryCreateRequestModel();
        given(categoryFacade.processCreateCategory(categoryCreateRequestModel)).willReturn(CATEGORY_ID);

        // when
        categoriesController.processCreateCategory(categoryCreateRequestModel, redirectAttributes);

        // then
        verify(categoryFacade).processCreateCategory(categoryCreateRequestModel);
        verifyFlashMessageSet();
        verifyRedirectionCreated(CATEGORY_VIEW_PATH);
    }

    @Test
    public void shouldProcessCreateCategoryHandleValidationFailure() throws CommunicationFailureException {

        // given
        CategoryCreateRequestModel categoryCreateRequestModel = new CategoryCreateRequestModel();
        doThrow(new ValidationFailureException(response)).when(categoryFacade).processCreateCategory(categoryCreateRequestModel);

        // when
        categoriesController.processCreateCategory(categoryCreateRequestModel, redirectAttributes);

        // then
        verifyValidationViolationInfoSet(categoryCreateRequestModel);
        verifyRedirectionCreated(PATH_CATEGORIES_CREATE);
    }

    @Test
    public void shouldShowCategoryEditForm() throws CommunicationFailureException {

        // when
        categoriesController.showCategoryEditForm(CATEGORY_ID);

        // then
        verify(categoryFacade).getCategory(CATEGORY_ID);
        verifyViewCreated(VIEW_EDIT_FORM);
        verifyFieldsSet(FIELD_CATEGORY);
    }

    @Test
    public void shouldProcessCategoryEdit() throws CommunicationFailureException {

        // given
        CategoryCreateRequestModel categoryCreateRequestModel = new CategoryCreateRequestModel();

        // when
        categoriesController.processCategoryEdit(CATEGORY_ID, categoryCreateRequestModel, redirectAttributes);

        // then
        verify(categoryFacade).processEditCategory(CATEGORY_ID, categoryCreateRequestModel);
        verifyFlashMessageSet();
        verifyRedirectionCreated(CATEGORY_VIEW_PATH);
    }

    @Test
    public void shouldProcessCategoryEditHandleValidationFailure() throws CommunicationFailureException {

        // given
        CategoryCreateRequestModel categoryCreateRequestModel = new CategoryCreateRequestModel();
        doThrow(new ValidationFailureException(response)).when(categoryFacade).processEditCategory(CATEGORY_ID, categoryCreateRequestModel);

        // when
        categoriesController.processCategoryEdit(CATEGORY_ID, categoryCreateRequestModel, redirectAttributes);

        // then
        verifyValidationViolationInfoSet(categoryCreateRequestModel);
        verifyRedirectionCreated(CATEGORY_VIEW_PATH);
    }

    @Test
    public void shouldProcessDeleteCategory() throws CommunicationFailureException {

        // when
        categoriesController.processDeleteCategory(CATEGORY_ID, redirectAttributes);

        // then
        verify(categoryFacade).processDeleteCategory(CATEGORY_ID);
        verifyFlashMessageSet();
        verifyRedirectionCreated(PATH_CATEGORIES);
    }

    @Test
    public void shouldProcessChangeStatusWithEnabledStatusMessage() throws CommunicationFailureException {

        // given
        given(categoryFacade.processChangeStatus(CATEGORY_ID)).willReturn(true);

        // when
        categoriesController.processChangeCategoryStatus(CATEGORY_ID, redirectAttributes);

        // then
        verify(categoryFacade).processChangeStatus(CATEGORY_ID);
        verifyStatusFlashMessage(true);
        verifyRedirectionCreated(CATEGORY_VIEW_PATH);
    }

    @Test
    public void shouldProcessChangeStatusWithDisabledStatusMessage() throws CommunicationFailureException {

        // given
        given(categoryFacade.processChangeStatus(CATEGORY_ID)).willReturn(false);

        // when
        categoriesController.processChangeCategoryStatus(CATEGORY_ID, redirectAttributes);

        // then
        verify(categoryFacade).processChangeStatus(CATEGORY_ID);
        verifyStatusFlashMessage(false);
        verifyRedirectionCreated(CATEGORY_VIEW_PATH);
    }

    @Override
    String controllerViewGroup() {
        return CATEGORIES;
    }
}