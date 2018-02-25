package hu.psprog.leaflet.lms.service.domain.tlp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * Domain object for parsing and storing received log events.
 *
 * @author Peter Smith
 */
@JsonDeserialize(builder = LoggingEvent.LoggingEventBuilder.class)
public class LoggingEvent {

    private String threadName;
    private String loggerName;
    private String level;
    private String content;
    private ThrowableProxyLogItem exception;
    private Date timeStamp;
    private String source;

    public String getThreadName() {
        return threadName;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public String getLevel() {
        return level;
    }

    public String getContent() {
        return content;
    }

    public ThrowableProxyLogItem getException() {
        return exception;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getSource() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        LoggingEvent that = (LoggingEvent) o;

        return new EqualsBuilder()
                .append(threadName, that.threadName)
                .append(loggerName, that.loggerName)
                .append(level, that.level)
                .append(content, that.content)
                .append(exception, that.exception)
                .append(timeStamp, that.timeStamp)
                .append(source, that.source)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(threadName)
                .append(loggerName)
                .append(level)
                .append(content)
                .append(exception)
                .append(timeStamp)
                .append(source)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("threadName", threadName)
                .append("loggerName", loggerName)
                .append("level", level)
                .append("content", content)
                .append("exception", exception)
                .append("timeStamp", timeStamp)
                .append("source", source)
                .toString();
    }

    public static LoggingEventBuilder getBuilder() {
        return new LoggingEventBuilder();
    }

    /**
     * Builder for {@link LoggingEvent}.
     */
    public static final class LoggingEventBuilder {
        private String threadName;
        private String loggerName;
        private String level;
        private String content;
        private ThrowableProxyLogItem exception;
        private Date timeStamp;
        private String source;

        private LoggingEventBuilder() {
        }

        public LoggingEventBuilder withThreadName(String threadName) {
            this.threadName = threadName;
            return this;
        }

        public LoggingEventBuilder withLoggerName(String loggerName) {
            this.loggerName = loggerName;
            return this;
        }

        public LoggingEventBuilder withLevel(String level) {
            this.level = level;
            return this;
        }

        public LoggingEventBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public LoggingEventBuilder withException(ThrowableProxyLogItem exception) {
            this.exception = exception;
            return this;
        }

        public LoggingEventBuilder withTimeStamp(Date timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public LoggingEventBuilder withSource(String source) {
            this.source = source;
            return this;
        }

        public LoggingEvent build() {
            LoggingEvent loggingEvent = new LoggingEvent();
            loggingEvent.threadName = this.threadName;
            loggingEvent.source = this.source;
            loggingEvent.level = this.level;
            loggingEvent.content = this.content;
            loggingEvent.timeStamp = this.timeStamp;
            loggingEvent.exception = this.exception;
            loggingEvent.loggerName = this.loggerName;
            return loggingEvent;
        }
    }
}
