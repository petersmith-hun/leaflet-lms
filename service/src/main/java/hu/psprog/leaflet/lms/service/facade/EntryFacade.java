package hu.psprog.leaflet.lms.service.facade;

import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EditEntryDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EntryListDataModel;
import hu.psprog.leaflet.bridge.client.domain.OrderBy;
import hu.psprog.leaflet.bridge.client.domain.OrderDirection;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.lms.service.domain.entry.EntryFormContent;
import hu.psprog.leaflet.lms.service.domain.entry.ModifyEntryRequest;

/**
 * Entry operations facade.
 *
 * @author Peter Smith
 */
public interface EntryFacade {

    /**
     * Returns paginated list of entries.
     *
     * @param page page number (page indexing starts at 1)
     * @param limit number of entries on one page
     * @param orderBy order by {@link OrderBy.Entry} options
     * @param orderDirection order direction (ASC|DESC)
     * @return page of entries wrapped in {@link WrapperBodyDataModel<EntryListDataModel>} object
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    WrapperBodyDataModel<EntryListDataModel> getEntries(int page, int limit, OrderBy.Entry orderBy, OrderDirection orderDirection) throws CommunicationFailureException;

    /**
     * Returns entry specified by given ID.
     *
     * @param entryID ID of the entry to retrieve
     * @return entry wrapped in {@link WrapperBodyDataModel<EditEntryDataModel>} object
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    WrapperBodyDataModel<EditEntryDataModel> getEntry(Long entryID) throws CommunicationFailureException;

    /**
     * Fills entry creation form with required data (ex.: tags, categories).
     *
     * @return filled {@link EntryFormContent} instance
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    EntryFormContent fillForm() throws CommunicationFailureException;

    /**
     * Fills entry edit form with required data (ex.: tags, categories). Also fills form with the data of given existing entry.
     *
     * @param entryID ID of entry to get data of
     * @return filled {@link EntryFormContent} instance
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    EntryFormContent fillForm(Long entryID) throws CommunicationFailureException;

    /**
     * Processes entity creation request.
     *
     * @param modifyEntryRequest entry data
     * @return ID of the created entry
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    Long processCreateEntry(ModifyEntryRequest modifyEntryRequest) throws CommunicationFailureException;

    /**
     * Processes entity editing request.
     *
     * @param id ID of the edited entry
     * @param modifyEntryRequest entry data
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    void processEditEntry(Long id, ModifyEntryRequest modifyEntryRequest) throws CommunicationFailureException;

    /**
     * Processes entity status change (enabled/disabled) request.
     *
     * @param id ID of the changed entry
     * @return current status as boolean, {@code true} if enabled, {@code false} otherwise
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    boolean processStatusChange(Long id) throws CommunicationFailureException;

    /**
     * Processes entry deletion request.
     *
     * @param id ID of the deleted entry
     * @throws CommunicationFailureException if Bridge fails to reach Leaflet
     */
    void processDeletion(Long id) throws CommunicationFailureException;
}
