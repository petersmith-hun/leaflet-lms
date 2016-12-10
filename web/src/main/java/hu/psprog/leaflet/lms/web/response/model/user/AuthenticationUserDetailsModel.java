package hu.psprog.leaflet.lms.web.response.model.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    private Integer id;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
