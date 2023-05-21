package hu.psprog.leaflet.lms.service.domain.entry;

import hu.psprog.leaflet.api.rest.request.entry.EntryInitialStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * Entry create/edit local request model.
 *
 * @author Peter Smith
 */
@Data
public class ModifyEntryRequest implements Serializable {

    @NotNull
    @NotEmpty
    @Size(max = 255)
    private String title;

    @NotNull
    @NotEmpty
    @Size(max = 255)
    private String link;

    @NotNull
    @NotEmpty
    private String prologue;

    @NotNull
    @NotEmpty
    private String rawContent;

    @NotNull
    @Min(1)
    private Long categoryID;

    @NotNull
    private Locale locale;

    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
    private boolean enabled;
    private EntryInitialStatus status = EntryInitialStatus.DRAFT;
    private List<Long> tags;
    private List<UUID> attachments;
    private Long userID;

    public List<Long> getTags() {
        return Optional.ofNullable(tags)
                .orElse(Collections.emptyList());
    }

    public List<UUID> getAttachments() {
        return Optional.ofNullable(attachments)
                .orElse(Collections.emptyList());
    }

}
