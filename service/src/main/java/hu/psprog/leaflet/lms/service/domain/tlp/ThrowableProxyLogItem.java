package hu.psprog.leaflet.lms.service.domain.tlp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * Exception (throwableProxy) node deserialization model that conforms Logback-original log event model format.
 *
 * @author Peter Smith
 */
@JsonDeserialize(builder = ThrowableProxyLogItem.ThrowableProxyLogItemBuilder.class)
public class ThrowableProxyLogItem {

    private String className;
    private String message;
    private String stackTrace;
    private ThrowableProxyLogItem cause;
    private List<ThrowableProxyLogItem> suppressed;

    public String getClassName() {
        return className;
    }

    public String getMessage() {
        return message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public ThrowableProxyLogItem getCause() {
        return cause;
    }

    public List<ThrowableProxyLogItem> getSuppressed() {
        return suppressed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ThrowableProxyLogItem that = (ThrowableProxyLogItem) o;

        return new EqualsBuilder()
                .append(className, that.className)
                .append(message, that.message)
                .append(stackTrace, that.stackTrace)
                .append(cause, that.cause)
                .append(suppressed, that.suppressed)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(className)
                .append(message)
                .append(stackTrace)
                .append(cause)
                .append(suppressed)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("className", className)
                .append("message", message)
                .append("stackTrace", stackTrace)
                .append("cause", cause)
                .append("suppressed", suppressed)
                .toString();
    }

    public static ThrowableProxyLogItemBuilder getBuilder() {
        return new ThrowableProxyLogItemBuilder();
    }

    /**
     * Builder for {@link ThrowableProxyLogItem}.
     */
    public static final class ThrowableProxyLogItemBuilder {
        private String className;
        private String message;
        private String stackTrace;
        private ThrowableProxyLogItem cause;
        private List<ThrowableProxyLogItem> suppressed;

        private ThrowableProxyLogItemBuilder() {
        }

        public ThrowableProxyLogItemBuilder withClassName(String className) {
            this.className = className;
            return this;
        }

        public ThrowableProxyLogItemBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public ThrowableProxyLogItemBuilder withStackTrace(String stackTrace) {
            this.stackTrace = stackTrace;
            return this;
        }

        public ThrowableProxyLogItemBuilder withCause(ThrowableProxyLogItem cause) {
            this.cause = cause;
            return this;
        }

        public ThrowableProxyLogItemBuilder withSuppressed(List<ThrowableProxyLogItem> suppressed) {
            this.suppressed = suppressed;
            return this;
        }

        public ThrowableProxyLogItem build() {
            ThrowableProxyLogItem throwableProxyLogItem = new ThrowableProxyLogItem();
            throwableProxyLogItem.className = this.className;
            throwableProxyLogItem.stackTrace = this.stackTrace;
            throwableProxyLogItem.suppressed = this.suppressed;
            throwableProxyLogItem.message = this.message;
            throwableProxyLogItem.cause = this.cause;
            return throwableProxyLogItem;
        }
    }
}
