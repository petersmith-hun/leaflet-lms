package hu.psprog.leaflet.lms.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Peter Smith
 */
@Controller
@RequestMapping("/")
public class DashboardController {

    @RequestMapping(method = RequestMethod.GET)
    public String home() {

        return "view/dashboard/home";
    }
}
