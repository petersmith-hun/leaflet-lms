package hu.psprog.leaflet.lms.web.menu.parser;

import hu.psprog.leaflet.lms.web.menu.domain.SystemMenu;

/**
 * {@link SystemMenu} parser interface.
 * Implementation should read up menu configuration and build a {@link SystemMenu} object based on that.
 *
 * @author Peter Smith
 */
public interface SystemMenuParser {

    /**
     * Parses menu configuration.
     *
     * @return built {@link SystemMenu} instance
     */
    SystemMenu parse();
}
