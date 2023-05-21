package hu.psprog.leaflet.lms.web.config;

import hu.psprog.leaflet.lms.web.menu.domain.SystemMenu;
import hu.psprog.leaflet.lms.web.menu.parser.SystemMenuParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Objects;

/**
 * {@link SystemMenu} configuration.
 * This configuration class triggers {@link SystemMenu} parsing on system startup.
 *
 * @author Peter Smith
 */
@Configuration
public class SystemMenuConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemMenuConfiguration.class);
    private static final String SYSTEM_MENU_SUCCESSFULLY_INITIALIZED = "System menu successfully initialized with %d top level menu items and %s sub level menu items.";

    private final SystemMenuParser systemMenuParser;

    @Autowired
    public SystemMenuConfiguration(SystemMenuParser systemMenuParser) {
        this.systemMenuParser = systemMenuParser;
    }

    @Bean
    public SystemMenu menu() throws IOException {

        SystemMenu menu = systemMenuParser.parse();
        LOGGER.info(String.format(SYSTEM_MENU_SUCCESSFULLY_INITIALIZED, countTopLevelMenuItems(menu), countSubMenuItems(menu)));

        return menu;
    }

    private int countTopLevelMenuItems(SystemMenu systemMenu) {
        return systemMenu.getMenu().size();
    }

    private long countSubMenuItems(SystemMenu systemMenu) {
        return systemMenu.getMenu().stream()
                .filter(menuItem -> Objects.nonNull(menuItem.getChildren()))
                .mapToLong(menuItem -> menuItem.getChildren().size())
                .sum();
    }
}
