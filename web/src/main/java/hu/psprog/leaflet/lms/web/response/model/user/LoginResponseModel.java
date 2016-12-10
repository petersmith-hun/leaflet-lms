package hu.psprog.leaflet.lms.web.response.model.user;

/**
 * Login response model, containing authentication status and the token if authentication was successful.
 *
 * @author Peter Smith
 */
public class LoginResponseModel {

    public enum AuthenticationResult {
        AUTH_SUCCESS,
        INVALID_CREDENTIALS
    }

    private AuthenticationResult result;
    private String token;

    public LoginResponseModel() {
    }

    public LoginResponseModel(AuthenticationResult result, String token) {
        this.result = result;
        this.token = token;
    }

    public AuthenticationResult getResult() {
        return result;
    }

    public String getToken() {
        return token;
    }
}
