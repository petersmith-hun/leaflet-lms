package hu.psprog.leaflet.lms.service.facade.adapter.dcp;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;

/**
 * DCP to specific domain and back adapter.
 * Able to collect DCP settings under separated keys to a specific domain, and also able to map a domain to its relevant separate keys.
 *
 * @param <T> type of the domain to collect data for
 * @author Peter Smith
 */
public interface DCPAdapter<T> {

    /**
     * Collects values under relevant keys into the specified domain.
     *
     * @return T domain of DCP settings
     * @throws CommunicationFailureException when the adapter fails to reach the backend
     */
    T collect() throws CommunicationFailureException;

    /**
     * Maps T domain of DCP settings to its relevant keys.
     *
     * @param data domain data
     * @throws CommunicationFailureException when the adapter fails to reach the backend
     */
    void update(T data) throws CommunicationFailureException;
}
