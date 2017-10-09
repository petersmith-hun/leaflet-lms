package hu.psprog.leaflet.lms.service.facade;

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
}
