package hu.psprog.leaflet.lms.service.exception;

/**
 * Exception to throw when LMS fails to reach CBFS failover application.
 *
 * @author Peter Smith
 */
public class FailoverCommunicationException extends Exception {

    public FailoverCommunicationException(String message) {
        super(message);
    }

    public FailoverCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
