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

    List<TranslationPackMetaInfo> getPacks();

    TranslationPack getPack(UUID packID);

    UUID processCreatePack(TranslationPackUploadRequestModel translationPackUploadRequestModel);

    void processDeletePack(UUID packID);

    boolean processChangePackStatus(UUID packID);
}
