package hu.psprog.leaflet.lms.web.controller.pagination.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Pagination attributes wrapper object.
 *
 * @author Peter Smith
 */
public class PaginationAttributes {

    private int pageCount;
    private int pageNumber;
    private boolean hasNext;
    private boolean hasPrevious;
    private int limit;
    private String orderBy;
    private String orderDirection;

    public int getPageCount() {
        return pageCount;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public int getLimit() {
        return limit;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public String getOrderDirection() {
        return orderDirection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PaginationAttributes that = (PaginationAttributes) o;

        return new EqualsBuilder()
                .append(pageCount, that.pageCount)
                .append(pageNumber, that.pageNumber)
                .append(hasNext, that.hasNext)
                .append(hasPrevious, that.hasPrevious)
                .append(limit, that.limit)
                .append(orderBy, that.orderBy)
                .append(orderDirection, that.orderDirection)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(pageCount)
                .append(pageNumber)
                .append(hasNext)
                .append(hasPrevious)
                .append(limit)
                .append(orderBy)
                .append(orderDirection)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("pageCount", pageCount)
                .append("pageNumber", pageNumber)
                .append("hasNext", hasNext)
                .append("hasPrevious", hasPrevious)
                .append("limit", limit)
                .append("orderBy", orderBy)
                .append("orderDirection", orderDirection)
                .toString();
    }

    public static PaginationAttributesBuilder getBuilder() {
        return new PaginationAttributesBuilder();
    }

    /**
     * Builder for {@link PaginationAttributes}.
     */
    public static final class PaginationAttributesBuilder {
        private int pageCount;
        private int pageNumber;
        private boolean hasNext;
        private boolean hasPrevious;
        private int limit;
        private String orderBy;
        private String orderDirection;

        private PaginationAttributesBuilder() {
        }

        public PaginationAttributesBuilder withPageCount(int pageCount) {
            this.pageCount = pageCount;
            return this;
        }

        public PaginationAttributesBuilder withPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
            return this;
        }

        public PaginationAttributesBuilder withHasNext(boolean hasNext) {
            this.hasNext = hasNext;
            return this;
        }

        public PaginationAttributesBuilder withHasPrevious(boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
            return this;
        }

        public PaginationAttributesBuilder withLimit(int limit) {
            this.limit = limit;
            return this;
        }

        public PaginationAttributesBuilder withOrderBy(String orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        public PaginationAttributesBuilder withOrderDirection(String orderDirection) {
            this.orderDirection = orderDirection;
            return this;
        }

        public PaginationAttributes build() {
            PaginationAttributes paginationAttributes = new PaginationAttributes();
            paginationAttributes.pageNumber = this.pageNumber;
            paginationAttributes.orderBy = this.orderBy;
            paginationAttributes.orderDirection = this.orderDirection;
            paginationAttributes.hasPrevious = this.hasPrevious;
            paginationAttributes.limit = this.limit;
            paginationAttributes.pageCount = this.pageCount;
            paginationAttributes.hasNext = this.hasNext;
            return paginationAttributes;
        }
    }
}
