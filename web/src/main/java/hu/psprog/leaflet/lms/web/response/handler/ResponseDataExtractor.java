package hu.psprog.leaflet.lms.web.response.handler;

import java.util.Map;

/**
 * In some cases response should be read up by the LMS before/without passing to the view.
 * {@link ResponseDataExtractor} classes can be used to extract (a part of) the answer and convert it to a model which can be used for further processing.
 *
 * @author Peter Smith
 */
public interface ResponseDataExtractor<T> {

    /**
     * Extracts a given part of the response Map.
     *
     * @param response original response map
     * @return T type of extracted data
     */
    T extract(Map<String, Object> response);
}
