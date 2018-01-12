package hu.psprog.leaflet.lms.service.auth.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import hu.psprog.leaflet.lms.service.auth.handler.deserializer.JWTExpirationDateDeserializer;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * JWT token payload wrapper model.
 *
 * @author Peter Smith
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticationUserDetailsModel {

    @JsonProperty("name")
    private String name;

    @JsonProperty("rol")
    private String role;

    @JsonProperty("uid")
    private Long id;

    @JsonProperty("exp")
    @JsonDeserialize(using = JWTExpirationDateDeserializer.class)
    private Date expiration;

    public AuthenticationUserDetailsModel() {
        // prevent direct initialization
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AuthenticationUserDetailsModel that = (AuthenticationUserDetailsModel) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(role, that.role)
                .append(id, that.id)
                .append(expiration, that.expiration)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(role)
                .append(id)
                .append(expiration)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("role", role)
                .append("id", id)
                .append("expiration", expiration)
                .toString();
    }
}
