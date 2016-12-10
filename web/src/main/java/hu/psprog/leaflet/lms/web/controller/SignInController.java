package hu.psprog.leaflet.lms.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Peter Smith
 */
@Controller
@RequestMapping("/login")
public class SignInController {

    @RequestMapping
    public String showLoginForm() {

        return "view/users/login";
    }
}
