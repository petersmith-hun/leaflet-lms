package hu.psprog.leaflet.lms.service.facade.adapter.dcp.impl;

import hu.psprog.leaflet.api.rest.request.dcp.DCPRequestModel;
import hu.psprog.leaflet.api.rest.response.dcp.DCPDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.DCPStoreBridgeService;
import hu.psprog.leaflet.lms.service.facade.adapter.dcp.DCPAdapter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Abstract {@link DCPAdapter} implementation for common operations used be all adapters.
 *
 * @author Peter Smith
 */
abstract class AbstractDCPAdapter<T> implements DCPAdapter<T> {

    @Autowired
    private DCPStoreBridgeService dcpStoreBridgeService;

    @Override
    public T collect() throws CommunicationFailureException {

        List<DCPDataModel> dcpEntries = getDCPEntries();

        return dcpEntries.isEmpty()
                ? emptyInstance().get()
                : collectToDomain(dcpEntries);
    }

    @Override
    public void update(T data) throws CommunicationFailureException {
        List<String> existingDCPKeys = getExistingDCPKeys();
        for (DCPDataModel dataItem : mapToDataModel(data)) {
            DCPRequestModel dcpRequestModel = mapDCPDataModelToRequestModel(dataItem);
            if (existingDCPKeys.contains(dataItem.getKey())) {
                dcpStoreBridgeService.updateDCPEntry(dcpRequestModel);
            } else {
                dcpStoreBridgeService.createDCPEntry(dcpRequestModel);
            }
        }
    }

    String extractValue(List<DCPDataModel> dcpDataModelList, Enum key) {
        return dcpDataModelList.stream()
                .filter(dcpDataModel -> dcpDataModel.getKey().equals(key.name()))
                .findFirst()
                .map(DCPDataModel::getValue)
                .orElse(null);
    }

    /**
     * Maps a domain object to list of {@link DCPDataModel} items.
     *
     * @param data domain data
     * @return List of {@link DCPDataModel}
     */
    abstract List<DCPDataModel> mapToDataModel(T data);

    /**
     * Collects required values for given domain.
     *
     * @param dataItems {@link List} of {@link DCPDataModel} objects to collect data from
     * @return created T domain object from collected data
     */
    abstract T collectToDomain(List<DCPDataModel> dataItems);

    /**
     * Creates an empty instance if there's no data in DCP.
     *
     * @return an empty instance of given domain object
     */
    abstract Supplier<T> emptyInstance();

    private DCPRequestModel mapDCPDataModelToRequestModel(DCPDataModel dcpDataModel) {

        DCPRequestModel dcpRequestModel = new DCPRequestModel();
        dcpRequestModel.setKey(dcpDataModel.getKey());
        dcpRequestModel.setValue(dcpDataModel.getValue());

        return dcpRequestModel;
    }

    private List<String> getExistingDCPKeys() throws CommunicationFailureException {
        return getDCPEntries().stream()
                .map(DCPDataModel::getKey)
                .collect(Collectors.toList());
    }

    private List<DCPDataModel> getDCPEntries() throws CommunicationFailureException {
        return Optional.ofNullable(dcpStoreBridgeService.getAllDCPEntries().getDcpStore())
                .orElseGet(Collections::emptyList);
    }
}
