package hu.psprog.leaflet.lms.web.controller.pagination;

import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.lms.web.controller.pagination.model.PaginationAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Common abstract implementation for extracting generic pagination model.
 *
 * @author Peter Smith
 */
abstract class AbstractStandardPaginationHelper<T extends Enum> extends PaginationHelper<T> {

    /**
     * Extract {@link PaginationAttributes} based on given {@link WrapperBodyDataModel} and {@link HttpServletRequest}.
     *
     * @param wrapperBodyDataModel {@link WrapperBodyDataModel} object to extract pagination attributes received from backend
     * @param request {@link HttpServletRequest} object to extract pagination attributes from current request
     * @return populated {@link PaginationAttributes} object
     */
    public PaginationAttributes extractPaginationAttributes(WrapperBodyDataModel<?> wrapperBodyDataModel, HttpServletRequest request) {

        return PaginationAttributes.getBuilder()
                .withPageCount(wrapperBodyDataModel.getPagination().getPageCount())
                .withPageNumber(wrapperBodyDataModel.getPagination().getPageNumber())
                .withHasNext(wrapperBodyDataModel.getPagination().isHasNext())
                .withHasPrevious(wrapperBodyDataModel.getPagination().isHasPrevious())
                .withLimit(getLimit(request))
                .withOrderBy(mapOrderBy(request))
                .withOrderDirection(mapOrderDirection(request))
                .build();
    }
}
