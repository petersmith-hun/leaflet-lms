package hu.psprog.leaflet.lms.web.menu.parser.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.psprog.leaflet.lms.web.menu.domain.SystemMenu;
import hu.psprog.leaflet.lms.web.menu.parser.SystemMenuParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

/**
 * Default implementation of {@link SystemMenuParser}.
 * This implementation simply reads up JSON menu configuration and builds the {@link SystemMenu} instance without any transformation.
 *
 * @author Peter Smith
 */
@Component
public class SimpleSystemMenuParser implements SystemMenuParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSystemMenuParser.class);
    private static final String FAILED_TO_PARSE_SYSTEM_MENU = "Failed to parse system menu.";
    private static final String MENU_CONFIG_LOCATION = "menu.json";

    private ObjectMapper objectMapper;

    @Autowired
    public SimpleSystemMenuParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public SystemMenu parse() {
        try {
            return objectMapper.readValue(getMenuConfiguration(), SystemMenu.class);
        } catch (IOException e) {
            LOGGER.error(FAILED_TO_PARSE_SYSTEM_MENU, e);
            throw new IllegalStateException(FAILED_TO_PARSE_SYSTEM_MENU, e);
        }
    }

    private URL getMenuConfiguration() throws IOException {
        return new ClassPathResource(MENU_CONFIG_LOCATION).getURL();
    }
}
