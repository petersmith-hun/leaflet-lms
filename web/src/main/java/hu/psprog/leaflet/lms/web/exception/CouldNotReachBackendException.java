package hu.psprog.leaflet.lms.web.exception;

/**
 * Exception to throw upon losing connection to Leaflet backend application.
 *
 * @author Peter Smith
 */
public class CouldNotReachBackendException extends RuntimeException {

    private static final String CONNECTION_LOST = "Connection lost to Leaflet.";

    public CouldNotReachBackendException(Throwable cause) {
        super(CONNECTION_LOST, cause);
    }
}
