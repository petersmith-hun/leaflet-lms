package hu.psprog.leaflet.lms.web.response.handler.impl;

import hu.psprog.leaflet.lms.web.response.handler.ResponseDataExtractor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Abstract implementation of {@link ResponseDataExtractor} interface.
 * Gives some commonly usable methods for {@link ResponseDataExtractor} implementations.
 *
 * @author Peter Smith
 */
public abstract class AbstractResponseDataExtractor<T> implements ResponseDataExtractor<T> {

    private static final String BODY = "body";

    /**
     * Extracts a String value from given node under key.
     *
     * @param node node to extract value from
     * @param key key of the value to be extracted
     * @return extracted value as String or null if not existing
     */
    protected String extractString(Map<String, Object> node, String key) {

        Object value = node.get(key);

        return Objects.isNull(value) ? null : String.valueOf(value);
    }

    /**
     * Extracts an integer value from given node under key.
     *
     * @param node node to extract value from
     * @param key key of the value to be extracted
     * @return extracted value as int or null if not existing
     */
    protected int extractInteger(Map<String, Object> node, String key) {

        Object value = node.get(key);

        return Objects.isNull(value) ? 0 : Integer.parseInt(value.toString());
    }

    /**
     * Extracts a boolean value from given node under key.
     *
     * @param node node to extract value from
     * @param key key of the value to be extracted
     * @return extracted value as boolean or null if not existing
     */
    protected boolean extractBoolean(Map<String, Object> node, String key) {

        Object value = node.get(key);

        return Objects.nonNull(value) && Boolean.parseBoolean(value.toString());
    }

    /**
     * Extracts a node value from given node under key.
     *
     * @param node node to extract sub node from
     * @param key key of the sub node to be extracted
     * @return extracted node as Map or empty map if not existing
     */
    protected Map<String, Object> extractNode(Map<String, Object> node, String key) {

        Object subNode = node.get(key);

        return subNode instanceof Map ? (Map) subNode : new HashMap<>();
    }

    /**
     * Extracts a list of nodes from given node under key.
     *
     * @param node node to extract list from
     * @param key key of the list to be extracted
     * @return extracted list as List of Maps or empty list if not existing
     */
    protected List<Map<String, Object>> extractList(Map<String, Object> node, String key) {

        Object subNode = node.get(key);

        return subNode instanceof List ? (List) subNode : new LinkedList<>();
    }

    /**
     * Extracts body node of the response.
     *
     * @param response original response map
     * @return content of body node as a Map
     */
    protected Map<String, Object> extractBody(Map<String, Object> response) {

        return extractNode(response, BODY);
    }
}
