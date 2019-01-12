package hu.psprog.leaflet.lms.service.domain.translations;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Locale;

/**
 * Request model for CSV based translation pack uploading.
 *
 * @author Peter Smith
 */
public class TranslationPackUploadRequestModel {

    @NotNull
    private Locale locale;

    @NotEmpty
    private String packName;

    @NotNull
    private MultipartFile definitions;

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public MultipartFile getDefinitions() {
        return definitions;
    }

    public void setDefinitions(MultipartFile definitions) {
        this.definitions = definitions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TranslationPackUploadRequestModel that = (TranslationPackUploadRequestModel) o;

        return new EqualsBuilder()
                .append(locale, that.locale)
                .append(packName, that.packName)
                .append(definitions, that.definitions)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(locale)
                .append(packName)
                .append(definitions)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("locale", locale)
                .append("packName", packName)
                .append("definitions", definitions)
                .toString();
    }

    public static TranslationPackUploadRequestModelBuilder getBuilder() {
        return new TranslationPackUploadRequestModelBuilder();
    }

    /**
     * Builder for {@link TranslationPackUploadRequestModel}.
     */
    public static final class TranslationPackUploadRequestModelBuilder {
        private Locale locale;
        private String packName;
        private MultipartFile definitions;

        private TranslationPackUploadRequestModelBuilder() {
        }

        public TranslationPackUploadRequestModelBuilder withLocale(Locale locale) {
            this.locale = locale;
            return this;
        }

        public TranslationPackUploadRequestModelBuilder withPackName(String packName) {
            this.packName = packName;
            return this;
        }

        public TranslationPackUploadRequestModelBuilder withDefinitions(MultipartFile definitions) {
            this.definitions = definitions;
            return this;
        }

        public TranslationPackUploadRequestModel build() {
            TranslationPackUploadRequestModel translationPackUploadRequestModel = new TranslationPackUploadRequestModel();
            translationPackUploadRequestModel.setLocale(locale);
            translationPackUploadRequestModel.setPackName(packName);
            translationPackUploadRequestModel.setDefinitions(definitions);
            return translationPackUploadRequestModel;
        }
    }
}
