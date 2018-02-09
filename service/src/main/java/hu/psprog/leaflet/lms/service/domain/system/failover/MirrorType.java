package hu.psprog.leaflet.lms.service.domain.system.failover;

/**
 * Possible mirror types.
 *
 * @author Peter Smith
 */
public enum MirrorType {

    /**
     * All mirrors (for logging Leaflet unavailability).
     */
    ALL,

    /**
     * Category list mirror.
     */
    CATEGORY,

    /**
     * Document list mirror.
     */
    DOCUMENT,

    /**
     * Entry list mirror.
     */
    ENTRY,

    /**
     * Entry page (categorized and non-categorized altogether) mirror.
     */
    ENTRY_PAGE
}
