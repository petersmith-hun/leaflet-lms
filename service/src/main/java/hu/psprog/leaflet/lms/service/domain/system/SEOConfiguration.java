package hu.psprog.leaflet.lms.service.domain.system;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * SEO configuration domain.
 *
 * @author Peter Smith
 */
public class SEOConfiguration implements Serializable {

    @Size(max = 255)
    private String pageTitle;

    @Size(max = 255)
    private String defaultTitle;

    @Size(max = 255)
    private String defaultDescription;

    @Size(max = 255)
    private String defaultKeywords;

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getDefaultTitle() {
        return defaultTitle;
    }

    public void setDefaultTitle(String defaultTitle) {
        this.defaultTitle = defaultTitle;
    }

    public String getDefaultDescription() {
        return defaultDescription;
    }

    public void setDefaultDescription(String defaultDescription) {
        this.defaultDescription = defaultDescription;
    }

    public String getDefaultKeywords() {
        return defaultKeywords;
    }

    public void setDefaultKeywords(String defaultKeywords) {
        this.defaultKeywords = defaultKeywords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SEOConfiguration that = (SEOConfiguration) o;

        return new EqualsBuilder()
                .append(pageTitle, that.pageTitle)
                .append(defaultTitle, that.defaultTitle)
                .append(defaultDescription, that.defaultDescription)
                .append(defaultKeywords, that.defaultKeywords)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(pageTitle)
                .append(defaultTitle)
                .append(defaultDescription)
                .append(defaultKeywords)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("pageTitle", pageTitle)
                .append("defaultTitle", defaultTitle)
                .append("defaultDescription", defaultDescription)
                .append("defaultKeywords", defaultKeywords)
                .toString();
    }

    public static SEOConfigurationBuilder getBuilder() {
        return new SEOConfigurationBuilder();
    }

    /**
     * Builder for {@link SEOConfiguration}.
     */
    public static final class SEOConfigurationBuilder {
        private String pageTitle;
        private String defaultTitle;
        private String defaultDescription;
        private String defaultKeywords;

        private SEOConfigurationBuilder() {
        }

        public SEOConfigurationBuilder withPageTitle(String pageTitle) {
            this.pageTitle = pageTitle;
            return this;
        }

        public SEOConfigurationBuilder withDefaultTitle(String defaultTitle) {
            this.defaultTitle = defaultTitle;
            return this;
        }

        public SEOConfigurationBuilder withDefaultDescription(String defaultDescription) {
            this.defaultDescription = defaultDescription;
            return this;
        }

        public SEOConfigurationBuilder withDefaultKeywords(String defaultKeywords) {
            this.defaultKeywords = defaultKeywords;
            return this;
        }

        public SEOConfiguration build() {
            SEOConfiguration sEOConfiguration = new SEOConfiguration();
            sEOConfiguration.setPageTitle(pageTitle);
            sEOConfiguration.setDefaultTitle(defaultTitle);
            sEOConfiguration.setDefaultDescription(defaultDescription);
            sEOConfiguration.setDefaultKeywords(defaultKeywords);
            return sEOConfiguration;
        }
    }
}
