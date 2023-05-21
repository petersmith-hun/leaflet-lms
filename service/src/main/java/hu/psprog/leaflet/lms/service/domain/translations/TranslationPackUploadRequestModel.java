package hu.psprog.leaflet.lms.service.domain.translations;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

/**
 * Request model for CSV based translation pack uploading.
 *
 * @author Peter Smith
 */
@Data
public class TranslationPackUploadRequestModel {

    @NotNull
    private Locale locale;

    @NotEmpty
    private String packName;

    @NotNull
    private MultipartFile definitions;

}
