package hu.psprog.leaflet.lms.web.response.handler.impl;

import hu.psprog.leaflet.lms.web.response.model.user.LoginResponseModel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * Extracts authentication status and token on successful authentication and wraps as {@link LoginResponseModel} object.
 *
 * @author Peter Smith
 */
@Component
public class AuthenticationResponseDataExtractor extends AbstractResponseDataExtractor<LoginResponseModel> {

    private static final String STATUS = "status";
    private static final String TOKEN = "token";

    @Override
    public LoginResponseModel extract(Map<String, Object> response) {

        LoginResponseModel loginResponseModel = null;
        Map<String, Object> content = extractBody(response);
        if (Objects.nonNull(content.get(STATUS))) {
            LoginResponseModel.AuthenticationResult authenticationResult =
                    LoginResponseModel.AuthenticationResult.valueOf(extractString(content, STATUS));
            loginResponseModel = new LoginResponseModel(authenticationResult, extractString(content, TOKEN));
        }

        return loginResponseModel;
    }
}
