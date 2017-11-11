package hu.psprog.leaflet.lms.service.facade.impl;

import hu.psprog.leaflet.api.rest.request.category.CategoryCreateRequestModel;
import hu.psprog.leaflet.api.rest.response.category.CategoryDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.CategoryBridgeService;
import hu.psprog.leaflet.lms.service.facade.CategoryFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link CategoryFacade}.
 *
 * @author Peter Smith
 */
@Service
public class CategoryFacadeImpl implements CategoryFacade {

    private CategoryBridgeService categoryBridgeService;

    @Autowired
    public CategoryFacadeImpl(CategoryBridgeService categoryBridgeService) {
        this.categoryBridgeService = categoryBridgeService;
    }

    @Override
    public List<CategoryDataModel> getAllCategories() throws CommunicationFailureException {
        return Optional.ofNullable(categoryBridgeService.getAllCategories().getCategories())
                .orElse(Collections.emptyList());
    }

    @Override
    public CategoryDataModel getCategory(Long categoryID) throws CommunicationFailureException {
        return categoryBridgeService.getCategory(categoryID);
    }

    @Override
    public Long processCreateCategory(CategoryCreateRequestModel categoryCreateRequestModel) throws CommunicationFailureException {
        return Optional.ofNullable(categoryBridgeService.createCategory(categoryCreateRequestModel))
                .map(CategoryDataModel::getId)
                .orElse(null);
    }

    @Override
    public void processEditCategory(Long categoryID, CategoryCreateRequestModel categoryCreateRequestModel) throws CommunicationFailureException {
        categoryBridgeService.updateCategory(categoryID, categoryCreateRequestModel);
    }

    @Override
    public boolean processChangeStatus(Long categoryID) throws CommunicationFailureException {
        return categoryBridgeService.changeStatus(categoryID).isEnabled();
    }

    @Override
    public void processDeleteCategory(Long categoryID) throws CommunicationFailureException {
        categoryBridgeService.deleteCategory(categoryID);
    }
}
