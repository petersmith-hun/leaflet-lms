package hu.psprog.leaflet.lms.service.domain.tlp;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Log retrieval request object.
 *
 * @author Peter Smith
 */
public class LogRequest {

    private boolean queried;
    private String source;
    private String level;
    private String content;
    private int page;
    private int limit;
    private OrderBy orderBy;
    private OrderDirection orderDirection;
    private String from;
    private String to;

    public boolean isQueried() {

        return queried;
    }

    public void setQueried(boolean queried) {
        this.queried = queried;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPage() {

        if (page == 0) {
            page = 1;
        }

        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public OrderDirection getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(OrderDirection orderDirection) {
        this.orderDirection = orderDirection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        LogRequest that = (LogRequest) o;

        return new EqualsBuilder()
                .append(queried, that.queried)
                .append(page, that.page)
                .append(limit, that.limit)
                .append(source, that.source)
                .append(level, that.level)
                .append(from, that.from)
                .append(to, that.to)
                .append(content, that.content)
                .append(orderBy, that.orderBy)
                .append(orderDirection, that.orderDirection)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(queried)
                .append(source)
                .append(level)
                .append(from)
                .append(to)
                .append(content)
                .append(page)
                .append(limit)
                .append(orderBy)
                .append(orderDirection)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("queried", queried)
                .append("source", source)
                .append("level", level)
                .append("from", from)
                .append("to", to)
                .append("content", content)
                .append("page", page)
                .append("limit", limit)
                .append("orderBy", orderBy)
                .append("orderDirection", orderDirection)
                .toString();
    }
}
