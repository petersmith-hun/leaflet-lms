package hu.psprog.leaflet.lms.service.domain.system.failover;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * Model object for status tracking entries.
 *
 * @author Peter Smith
 */
public class StatusEntry {

    private Date created;
    private FailoverStatus status;
    private String parameter;

    public Date getCreated() {
        return created;
    }

    public FailoverStatus getStatus() {
        return status;
    }

    public String getParameter() {
        return parameter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        StatusEntry that = (StatusEntry) o;

        return new EqualsBuilder()
                .append(created, that.created)
                .append(status, that.status)
                .append(parameter, that.parameter)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(created)
                .append(status)
                .append(parameter)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("created", created)
                .append("status", status)
                .append("parameter", parameter)
                .toString();
    }

    public static StatusEntryBuilder getBuilder() {
        return new StatusEntryBuilder();
    }

    /**
     * Builder for {@link StatusEntry}.
     */
    public static final class StatusEntryBuilder {
        private Date created;
        private FailoverStatus status;
        private String parameter;

        private StatusEntryBuilder() {
        }

        public StatusEntryBuilder withCreated(Date created) {
            this.created = created;
            return this;
        }

        public StatusEntryBuilder withStatus(FailoverStatus status) {
            this.status = status;
            return this;
        }

        public StatusEntryBuilder withParameter(String parameter) {
            this.parameter = parameter;
            return this;
        }

        public StatusEntry build() {
            StatusEntry statusEntry = new StatusEntry();
            statusEntry.parameter = this.parameter;
            statusEntry.created = this.created;
            statusEntry.status = this.status;
            return statusEntry;
        }
    }
}
