package hu.psprog.leaflet.lms.web.controller.pagination.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Pagination attributes for log viewer.
 *
 * @author Peter Smith
 */
public class LogViewerPaginationAttributes {

    private int limit;
    private String orderBy;
    private String orderDirection;
    private String source;
    private String level;
    private String content;
    private String from;
    private String to;

    public int getLimit() {
        return limit;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public String getOrderDirection() {
        return orderDirection;
    }

    public String getSource() {
        return source;
    }

    public String getLevel() {
        return level;
    }

    public String getContent() {
        return content;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        LogViewerPaginationAttributes that = (LogViewerPaginationAttributes) o;

        return new EqualsBuilder()
                .append(limit, that.limit)
                .append(orderBy, that.orderBy)
                .append(orderDirection, that.orderDirection)
                .append(source, that.source)
                .append(level, that.level)
                .append(content, that.content)
                .append(from, that.from)
                .append(to, that.to)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(limit)
                .append(orderBy)
                .append(orderDirection)
                .append(source)
                .append(level)
                .append(content)
                .append(from)
                .append(to)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("limit", limit)
                .append("orderBy", orderBy)
                .append("orderDirection", orderDirection)
                .append("source", source)
                .append("level", level)
                .append("content", content)
                .append("from", from)
                .append("to", to)
                .toString();
    }

    public static LogViewerPaginationAttributesBuilder getBuilder() {
        return new LogViewerPaginationAttributesBuilder();
    }

    /**
     * Builder for {@link LogViewerPaginationAttributes}.
     */
    public static final class LogViewerPaginationAttributesBuilder {
        private int limit;
        private String orderBy;
        private String orderDirection;
        private String source;
        private String level;
        private String content;
        private String from;
        private String to;

        private LogViewerPaginationAttributesBuilder() {
        }

        public LogViewerPaginationAttributesBuilder withLimit(int limit) {
            this.limit = limit;
            return this;
        }

        public LogViewerPaginationAttributesBuilder withOrderBy(String orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        public LogViewerPaginationAttributesBuilder withOrderDirection(String orderDirection) {
            this.orderDirection = orderDirection;
            return this;
        }

        public LogViewerPaginationAttributesBuilder withSource(String source) {
            this.source = source;
            return this;
        }

        public LogViewerPaginationAttributesBuilder withLevel(String level) {
            this.level = level;
            return this;
        }

        public LogViewerPaginationAttributesBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public LogViewerPaginationAttributesBuilder withFrom(String from) {
            this.from = from;
            return this;
        }

        public LogViewerPaginationAttributesBuilder withTo(String to) {
            this.to = to;
            return this;
        }

        public LogViewerPaginationAttributes build() {
            LogViewerPaginationAttributes logViewerPaginationAttributes = new LogViewerPaginationAttributes();
            logViewerPaginationAttributes.limit = this.limit;
            logViewerPaginationAttributes.from = this.from;
            logViewerPaginationAttributes.orderBy = this.orderBy;
            logViewerPaginationAttributes.source = this.source;
            logViewerPaginationAttributes.level = this.level;
            logViewerPaginationAttributes.to = this.to;
            logViewerPaginationAttributes.content = this.content;
            logViewerPaginationAttributes.orderDirection = this.orderDirection;
            return logViewerPaginationAttributes;
        }
    }
}
