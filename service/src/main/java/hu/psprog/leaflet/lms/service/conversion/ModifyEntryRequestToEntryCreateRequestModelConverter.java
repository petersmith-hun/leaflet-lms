package hu.psprog.leaflet.lms.service.conversion;

import hu.psprog.leaflet.api.rest.request.entry.EntryCreateRequestModel;
import hu.psprog.leaflet.lms.service.domain.entry.ModifyEntryRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts {@link ModifyEntryRequest} to {@link EntryCreateRequestModel}.
 *
 * @author Peter Smith
 */
@Component
public class ModifyEntryRequestToEntryCreateRequestModelConverter implements Converter<ModifyEntryRequest, EntryCreateRequestModel> {

    @Override
    public EntryCreateRequestModel convert(ModifyEntryRequest source) {

        EntryCreateRequestModel createRequestModel = new EntryCreateRequestModel();
        createRequestModel.setCategoryID(source.getCategoryID());
        createRequestModel.setContent(source.getGeneratedContent());
        createRequestModel.setEnabled(source.isEnabled());
        createRequestModel.setLink(source.getLink());
        createRequestModel.setLocale(source.getLocale());
        createRequestModel.setMetaDescription(source.getMetaDescription());
        createRequestModel.setMetaKeywords(source.getMetaKeywords());
        createRequestModel.setMetaTitle(source.getMetaTitle());
        createRequestModel.setPrologue(source.getPrologue());
        createRequestModel.setRawContent(source.getRawContent());
        createRequestModel.setStatus(source.getStatus());
        createRequestModel.setTitle(source.getTitle());
        createRequestModel.setUserID(source.getUserID());

        return createRequestModel;
    }
}
