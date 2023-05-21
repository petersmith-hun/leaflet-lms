package hu.psprog.leaflet.lms.web.controller.pagination;

import hu.psprog.leaflet.lms.web.controller.pagination.model.LogViewerPaginationAttributes;
import hu.psprog.leaflet.tlp.api.domain.OrderBy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * {@link PaginationHelper} implementation for generating pagination on log viewer page.
 *
 * @author Peter Smith
 */
@Component
public class LogViewerPaginationHelper extends PaginationHelper<OrderBy> {

    private static final OrderBy DEFAULT_ORDER_BY = OrderBy.TIMESTAMP;
    private static final String FILTER_FIELD_SOURCE = "source";
    private static final String FILTER_FIELD_LEVEL = "level";
    private static final String FILTER_FIELD_CONTENT = "content";
    private static final String FILTER_FIELD_FROM = "from";
    private static final String FILTER_FIELD_TO = "to";

    /**
     * Generates pagination attributes model based on current request.
     *
     * @param request {@link HttpServletRequest} object to extract current pagination settings
     * @return populated {@link LogViewerPaginationAttributes} object
     */
    public LogViewerPaginationAttributes extractPaginationAttributes(HttpServletRequest request) {
        return LogViewerPaginationAttributes.getBuilder()
                .withLimit(getLimit(request))
                .withOrderBy(mapOrderBy(request))
                .withOrderDirection(mapOrderDirection(request))
                .withSource(extractWithEmptyDefault(request, FILTER_FIELD_SOURCE))
                .withLevel(extractWithEmptyDefault(request, FILTER_FIELD_LEVEL))
                .withContent(extractWithEmptyDefault(request, FILTER_FIELD_CONTENT))
                .withFrom(extractWithEmptyDefault(request, FILTER_FIELD_FROM))
                .withTo(extractWithEmptyDefault(request, FILTER_FIELD_TO))
                .build();
    }

    @Override
    public OrderBy mapOrderBy(Optional<String> optionalOrderBy) {
        return mapToOrderBy(optionalOrderBy.orElse(DEFAULT_ORDER_BY.getField()));
    }

    private OrderBy mapToOrderBy(String orderBy) {
        return Stream.of(OrderBy.values())
                .filter(item -> item.getField().equalsIgnoreCase(orderBy))
                .findFirst()
                .orElse(DEFAULT_ORDER_BY);
    }

    private String extractWithEmptyDefault(HttpServletRequest request, String parameter) {
        return getFromRequest(request, parameter, Function.identity())
                .orElse(StringUtils.EMPTY);
    }
}
