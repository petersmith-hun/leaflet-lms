package hu.psprog.leaflet.lms.service.facade;

import hu.psprog.leaflet.lms.service.domain.translations.TranslationPackUploadRequestModel;
import hu.psprog.leaflet.translation.api.domain.TranslationPack;
import hu.psprog.leaflet.translation.api.domain.TranslationPackMetaInfo;

import java.util.List;
import java.util.UUID;

/**
 * Translation management operations facade.
 *
 * @author Peter Smith
 */
public interface TranslationManagementFacade {

    /**
     * Retrieves information of all existing translation packs.
     *
     * @return List of {@link TranslationPackMetaInfo}
     */
    List<TranslationPackMetaInfo> getPacks();

    /**
     * Retrieves translation pack identified by given ID.
     *
     * @param packID ID of the pack to retrieve
     * @return identified translation pack as {@link TranslationPack}
     */
    TranslationPack getPack(UUID packID);

    /**
     * Processes translation pack creation request.
     *
     * @param translationPackUploadRequestModel {@link TranslationPackUploadRequestModel} containing translation pack data
     * @return ID of created translation pack
     */
    UUID processCreatePack(TranslationPackUploadRequestModel translationPackUploadRequestModel);

    /**
     * Processes translation pack deletion request.
     *
     * @param packID ID of the translation pack to delete
     */
    void processDeletePack(UUID packID);

    /**
     * Processes translation pack status change request.
     *
     * @param packID ID of the translation pack to change status of
     * @return current status as boolean, {@code true} if enabled, {@code false} otherwise
     */
    boolean processChangePackStatus(UUID packID);
}
