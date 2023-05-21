package hu.psprog.leaflet.lms.service.domain.dashboard;

import java.util.Map;

/**
 * Base interface for model classes holding information for UI-side (JavaScript based) LSAS HTTP monitoring calls.
 *
 * @author Peter Smith
 */
public interface JSClientHelperModel {

    /**
     * Returns a boolean flag determining if the specific monitoring operation set is enabled.
     *
     * @return {@code true} if the monitoring operation set is enabled, {@code false} otherwise
     */
    boolean enabled();

    /**
     * Returns an authorization header for the monitoring operation set.
     *
     * @return an authorization header for the monitoring operation set as {@link Map} of {@link String} key and value pair(s)
     */
    Map<String, String> authorization();
}
