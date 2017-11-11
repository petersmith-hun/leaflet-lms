package hu.psprog.leaflet.lms.service.facade;

import hu.psprog.leaflet.api.rest.request.category.CategoryCreateRequestModel;
import hu.psprog.leaflet.api.rest.response.category.CategoryDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;

import java.util.List;

/**
 * Category operations facade.
 *
 * @author Peter Smith
 */
public interface CategoryFacade {

    /**
     * Returns list of all existing categories.
     *
     * @return list of categories
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    List<CategoryDataModel> getAllCategories() throws CommunicationFailureException;

    /**
     * Returns category identified by given ID.
     *
     * @param categoryID ID of the category to retrieve
     * @return category data as {@link CategoryDataModel}
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    CategoryDataModel getCategory(Long categoryID) throws CommunicationFailureException;

    /**
     * Processes category creation request.
     *
     * @param categoryCreateRequestModel category data
     * @return ID of the created category
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    Long processCreateCategory(CategoryCreateRequestModel categoryCreateRequestModel) throws CommunicationFailureException;

    /**
     * Processes category edit request.
     *
     * @param categoryID ID of the category to edit
     * @param categoryCreateRequestModel updated category data
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void processEditCategory(Long categoryID, CategoryCreateRequestModel categoryCreateRequestModel) throws CommunicationFailureException;

    /**
     * Processes category status change request.
     *
     * @param categoryID ID of the category to change status of
     * @return current status as boolean, {@code true} if enabled, {@code false} otherwise
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    boolean processChangeStatus(Long categoryID) throws CommunicationFailureException;

    /**
     * Processes category deletion request.
     *
     * @param categoryID ID of the category to delete
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void processDeleteCategory(Long categoryID) throws CommunicationFailureException;
}
