package hu.psprog.leaflet.lms.web.response.handler;

import java.util.Map;

/**
 * In some cases response should be read up by the LMS before/without passing to the view.
 * {@link ResponseDataExtractor} classes can be used to extract (a part of) the answer and convert it to a model which can be used for further processing.
 *
 * @author Peter Smith
 */
public abstract class ResponseDataExtractor<T> {

    private static final String BODY = "body";

    /**
     * Extracts a given part of the response Map.
     *
     * @param response original response map
     * @return T type of extracted data
     */
    public abstract T extract(Map<String, Object> response);

    /**
     * Extracts body node of the response.
     *
     * @param response original response map
     * @return content of body node as a Map
     */
    protected Map<String, Object> extractBody(Map<String, Object> response) {

        return response.get(BODY) instanceof Map ? (Map) response.get(BODY) : null;
    }
}
