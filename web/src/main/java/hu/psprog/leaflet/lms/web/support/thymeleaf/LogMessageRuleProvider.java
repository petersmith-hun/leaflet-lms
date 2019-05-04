package hu.psprog.leaflet.lms.web.support.thymeleaf;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Decides the box rule for a given log message upon rendering.
 *
 * @author Peter Smith
 */
@Component
public class LogMessageRuleProvider {

    private static final String DEFAULT_RULE = "box box-default";
    private static final Map<String, String> RULE_MAP = Map.of(
            "ERROR", "box box-danger",
            "WARN", "box box-warning",
            "INFO", "box box-success");

    /**
     * Returns the associated rule name for the given log level.
     * Returns default value for unknown log level.
     *
     * @param logLevel log level
     * @return associated rule name
     */
    public String getRule(String logLevel) {
        return RULE_MAP.getOrDefault(String.valueOf(logLevel).toUpperCase(), DEFAULT_RULE);
    }
}
