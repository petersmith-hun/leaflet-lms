package hu.psprog.leaflet.lms.service.domain.system.failover;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.List;

/**
 * Domain object for CBFS status requests.
 *
 * @author Peter Smith
 */
public class StatusResponse {

    private FailoverStatus status;
    private Date lastCall;
    private Date lastMirroring;
    private List<MirrorStatus> mirrorStatus;
    private List<StatusEntry> statusEntryList;

    public FailoverStatus getStatus() {
        return status;
    }

    public Date getLastCall() {
        return lastCall;
    }

    public Date getLastMirroring() {
        return lastMirroring;
    }

    public List<MirrorStatus> getMirrorStatus() {
        return mirrorStatus;
    }

    public List<StatusEntry> getStatusEntryList() {
        return statusEntryList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        StatusResponse that = (StatusResponse) o;

        return new EqualsBuilder()
                .append(status, that.status)
                .append(lastCall, that.lastCall)
                .append(lastMirroring, that.lastMirroring)
                .append(mirrorStatus, that.mirrorStatus)
                .append(statusEntryList, that.statusEntryList)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(status)
                .append(lastCall)
                .append(lastMirroring)
                .append(mirrorStatus)
                .append(statusEntryList)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("status", status)
                .append("lastCall", lastCall)
                .append("lastMirroring", lastMirroring)
                .append("mirrorStatus", mirrorStatus)
                .append("statusEntryList", statusEntryList)
                .toString();
    }

    public static StatusResponseBuilder getBuilder() {
        return new StatusResponseBuilder();
    }

    /**
     * Builder for {@link StatusResponseBuilder}.
     */
    public static final class StatusResponseBuilder {
        private FailoverStatus status;
        private Date lastCall;
        private Date lastMirroring;
        private List<MirrorStatus> mirrorStatus;
        private List<StatusEntry> statusEntryList;

        private StatusResponseBuilder() {
        }

        public StatusResponseBuilder withStatus(FailoverStatus status) {
            this.status = status;
            return this;
        }

        public StatusResponseBuilder withLastCall(Date lastCall) {
            this.lastCall = lastCall;
            return this;
        }

        public StatusResponseBuilder withLastMirroring(Date lastMirroring) {
            this.lastMirroring = lastMirroring;
            return this;
        }

        public StatusResponseBuilder withMirrorStatus(List<MirrorStatus> mirrorStatus) {
            this.mirrorStatus = mirrorStatus;
            return this;
        }

        public StatusResponseBuilder withStatusEntryList(List<StatusEntry> statusEntryList) {
            this.statusEntryList = statusEntryList;
            return this;
        }

        public StatusResponse build() {
            StatusResponse statusResponse = new StatusResponse();
            statusResponse.status = this.status;
            statusResponse.lastCall = this.lastCall;
            statusResponse.statusEntryList = this.statusEntryList;
            statusResponse.mirrorStatus = this.mirrorStatus;
            statusResponse.lastMirroring = this.lastMirroring;
            return statusResponse;
        }
    }
}
