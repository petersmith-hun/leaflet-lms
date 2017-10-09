package hu.psprog.leaflet.lms.web.controller.pagination;

import hu.psprog.leaflet.bridge.client.domain.OrderDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.function.Function;

/**
 * Pagination settings extractor.
 * Requires default values:
 *  - pagination.default.limit: default number of entities on one page
 *  - pagination.default.order-direction: default order direction
 *  - pagination.default.order-by: default field name to order by
 *
 * @param <T> field enum value of a paginated entity's available sort fields
 * @author Peter Smith
 */
public abstract class PaginationHelper<T extends Enum> {

    private static final String DESC = "desc";
    private static final int FIRST_PAGE = 1;
    public static final String PARAMETER_PAGE = "page";
    public static final String PARAMETER_LIMIT = "limit";
    public static final String PARAMETER_ORDER_BY = "orderBy";
    public static final String PARAMETER_ORDER_DIRECTION = "orderDirection";

    @Autowired
    private PaginationDefaults paginationDefaults;

    /**
     * Extracts page number by given page parameter.
     *
     * @param page original page number
     * @return given page number, or 1 if parameter is null
     */
    public int extractPage(Optional<Integer> page) {
        return page.orElse(FIRST_PAGE);
    }

    /**
     * Extracts "order direction" field from request object.
     * Defaults to {@code pagination.default.order-direction}
     *
     * @param request {@link HttpServletRequest} object
     * @return order direction
     */
    public OrderDirection mapOrderDirection(HttpServletRequest request) {
        return mapOrderDirection(getFromRequest(request, PARAMETER_ORDER_DIRECTION, Function.identity()));
    }

    /**
     * Extracts "order direction" field.
     * Defaults to {@code pagination.default.order-direction}
     *
     * @param optionalOrderDirection (optional) current order direction
     * @return order direction
     */
    public OrderDirection mapOrderDirection(Optional<String> optionalOrderDirection) {
        return optionalOrderDirection
                .map(orderDirection -> orderDirection.toLowerCase().equals(DESC)
                        ? OrderDirection.DESC
                        : OrderDirection.ASC)
                .orElse(paginationDefaults.getOrderDirection());
    }

    /**
     * Extracts current limit from request object.
     * Defaults to {@code pagination.default.limit}
     *
     * @param request {@link HttpServletRequest} object
     * @return limit value
     */
    public Integer getLimit(HttpServletRequest request) {
        return getLimit(getFromRequest(request, PARAMETER_LIMIT, Integer::valueOf));
    }

    /**
     * Extracts current limit.
     * Defaults to {@code pagination.default.limit}
     *
     * @param optionalLimit (optional) current limit
     * @return limit value
     */
    public Integer getLimit(Optional<Integer> optionalLimit) {
        return optionalLimit.orElse(paginationDefaults.getLimit());
    }

    /**
     * Extracts "order by" field for specific type of entities.
     * Implementation should default to {@code pagination.default.order-by}.
     *
     * @param request {@link HttpServletRequest} object
     * @return enum value to order by
     */
    public T mapOrderBy(HttpServletRequest request) {
        return mapOrderBy(getFromRequest(request, PARAMETER_ORDER_BY, Function.identity()));
    }

    /**
     * Extracts "order by" field for specific type of entities.
     * Implementation should default to {@code pagination.default.order-by}.
     *
     * @param optionalOrderBy (optional) current order by field name.
     * @return enum value to order by
     */
    public abstract T mapOrderBy(Optional<String> optionalOrderBy);

    /**
     * Returns default order by field name for {@link PaginationHelper#mapOrderBy} implementations.
     *
     * @return default order by field name
     */
    String defaultOrderBy() {
        return paginationDefaults.getOrderBy();
    }

    private <R> Optional<R> getFromRequest(HttpServletRequest request, String parameter, Function<String, R> valueMapper) {
        return Optional.ofNullable(request.getParameter(parameter))
                .map(valueMapper);
    }

    @Component
    @ConfigurationProperties(prefix = "pagination.default")
    class PaginationDefaults {

        private Integer limit;
        private OrderDirection orderDirection;
        private String orderBy;

        public Integer getLimit() {
            return limit;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }

        public OrderDirection getOrderDirection() {
            return orderDirection;
        }

        public void setOrderDirection(OrderDirection orderDirection) {
            this.orderDirection = orderDirection;
        }

        public String getOrderBy() {
            return orderBy;
        }

        public void setOrderBy(String orderBy) {
            this.orderBy = orderBy;
        }
    }
}
