package hu.psprog.leaflet.lms.web.controller.pagination;

import hu.psprog.leaflet.bridge.client.domain.OrderDirection;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Default pagination settings.
 *
 * @author Peter Smith
 */
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
