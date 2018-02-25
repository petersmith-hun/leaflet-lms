package hu.psprog.leaflet.lms.service.exception;

/**
 * Exception to throw when LMS fails to reach TLP application.
 *
 * @author Peter Smith
 */
public class TLPCommunicationException extends Exception {

    public TLPCommunicationException(String message) {
        super(message);
    }

    public TLPCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
