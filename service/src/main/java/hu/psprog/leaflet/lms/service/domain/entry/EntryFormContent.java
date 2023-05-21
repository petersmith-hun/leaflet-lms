package hu.psprog.leaflet.lms.service.domain.entry;

import hu.psprog.leaflet.api.rest.response.category.CategoryDataModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EditEntryDataModel;
import hu.psprog.leaflet.api.rest.response.tag.TagDataModel;
import hu.psprog.leaflet.lsrs.api.response.FileDataModel;
import lombok.Builder;

import java.util.List;

/**
 * Wrapper model for Entry creation/edit form data.
 *
 * @author Peter Smith
 */
@Builder
public record EntryFormContent(
        List<TagDataModel> existingTags,
        List<CategoryDataModel> existingCategories,
        List<FileDataModel> existingFiles,
        WrapperBodyDataModel<EditEntryDataModel> entryData,
        List<String> attachedFileReferences
) { }
