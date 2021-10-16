package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.category.CategoryCreateRequestModel;
import hu.psprog.leaflet.api.rest.response.category.CategoryDataModel;
import hu.psprog.leaflet.api.rest.response.category.CategoryListDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.CategoryBridgeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link CategoryFacadeImpl}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class CategoryFacadeImplTest {

    private static final Long CATEGORY_ID = 1L;
    private static final String TITLE = "category-1";

    @Mock
    private CategoryBridgeService categoryBridgeService;

    @InjectMocks
    private CategoryFacadeImpl categoryFacade;

    @Test
    public void shouldGetAllCategories() throws CommunicationFailureException {

        // given
        given(categoryBridgeService.getAllCategories()).willReturn(CategoryListDataModel.getBuilder()
                .withItem(CategoryDataModel.getBuilder().withTitle(TITLE).build())
                .build());

        // when
        List<CategoryDataModel> result = categoryFacade.getAllCategories();

        // then
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getTitle(), equalTo(TITLE));
    }

    @Test
    public void shouldReturnEmptyListIfNoCategoriesFound() throws CommunicationFailureException {

        // given
        given(categoryBridgeService.getAllCategories()).willReturn(CategoryListDataModel.getBuilder().build());

        // when
        List<CategoryDataModel> result = categoryFacade.getAllCategories();

        // then
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void shouldGetCategory() throws CommunicationFailureException {

        // given
        given(categoryBridgeService.getCategory(CATEGORY_ID)).willReturn(prepareCategoryDataModel());

        // when
        CategoryDataModel result = categoryFacade.getCategory(CATEGORY_ID);

        // then
        assertThat(result.getTitle(), equalTo(TITLE));
    }

    @Test
    public void shouldProcessCreate() throws CommunicationFailureException {

        // given
        given(categoryBridgeService.createCategory(any(CategoryCreateRequestModel.class))).willReturn(prepareCategoryDataModel());

        // when
        Long result = categoryFacade.processCreateCategory(new CategoryCreateRequestModel());

        // then
        assertThat(result, equalTo(CATEGORY_ID));
    }

    @Test
    public void shouldReturnNullOnCreateIfResponseIsNull() throws CommunicationFailureException {

        // given
        given(categoryBridgeService.createCategory(any(CategoryCreateRequestModel.class))).willReturn(null);

        // when
        Long result = categoryFacade.processCreateCategory(new CategoryCreateRequestModel());

        // then
        assertThat(result, nullValue());
    }

    @Test
    public void shouldProcessEditCategory() throws CommunicationFailureException {

        // given
        CategoryCreateRequestModel categoryCreateRequestModel = new CategoryCreateRequestModel();

        // when
        categoryFacade.processEditCategory(CATEGORY_ID, categoryCreateRequestModel);

        // then
        verify(categoryBridgeService).updateCategory(CATEGORY_ID, categoryCreateRequestModel);
    }

    @Test
    public void shouldProcessChangeStatus() throws CommunicationFailureException {

        // given
        given(categoryBridgeService.changeStatus(CATEGORY_ID)).willReturn(prepareCategoryDataModel());

        // when
        boolean result = categoryFacade.processChangeStatus(CATEGORY_ID);

        // then
        assertThat(result, is(true));
        verify(categoryBridgeService).changeStatus(CATEGORY_ID);
    }

    @Test
    public void shouldProcessDeleteCategory() throws CommunicationFailureException {

        // when
        categoryFacade.processDeleteCategory(CATEGORY_ID);

        // then
        verify(categoryBridgeService).deleteCategory(CATEGORY_ID);
    }

    private CategoryDataModel prepareCategoryDataModel() {
        return CategoryDataModel.getBuilder()
                .withID(CATEGORY_ID)
                .withTitle(TITLE)
                .withEnabled(true)
                .build();
    }
}