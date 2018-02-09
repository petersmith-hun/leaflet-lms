package hu.psprog.leaflet.lms.service.domain.system.failover;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Mirroring result containing mirror type and number of processed records.
 *
 * @author Peter Smith
 */
public class MirrorStatus {

    private MirrorType mirrorType;
    private int numberOfRecords;

    public MirrorType getMirrorType() {
        return mirrorType;
    }

    public int getNumberOfRecords() {
        return numberOfRecords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MirrorStatus that = (MirrorStatus) o;

        return new EqualsBuilder()
                .append(numberOfRecords, that.numberOfRecords)
                .append(mirrorType, that.mirrorType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mirrorType)
                .append(numberOfRecords)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("mirrorType", mirrorType)
                .append("numberOfRecords", numberOfRecords)
                .toString();
    }

    public static MirrorStatusBuilder getBuilder() {
        return new MirrorStatusBuilder();
    }

    /**
     * Builder for {@link MirrorStatus}.
     */
    public static final class MirrorStatusBuilder {
        private MirrorType mirrorType;
        private int numberOfRecords;

        private MirrorStatusBuilder() {
        }

        public MirrorStatusBuilder withMirrorType(MirrorType mirrorType) {
            this.mirrorType = mirrorType;
            return this;
        }

        public MirrorStatusBuilder withNumberOfRecords(int numberOfRecords) {
            this.numberOfRecords = numberOfRecords;
            return this;
        }

        public MirrorStatus build() {
            MirrorStatus mirrorStatus = new MirrorStatus();
            mirrorStatus.numberOfRecords = this.numberOfRecords;
            mirrorStatus.mirrorType = this.mirrorType;
            return mirrorStatus;
        }
    }
}
