package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.response.user.UserListDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.UserBridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Peter Smith
 */
@Controller
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UserBridgeService userBridgeService;

    @RequestMapping
    public ModelAndView usersList() throws Exception {

        try {
            UserListDataModel allUsers = userBridgeService.getAllUsers();
            ModelAndView modelAndView = new ModelAndView("view/users/list");
            modelAndView.addObject(allUsers);

            return modelAndView;
        } catch (CommunicationFailureException e) {
            throw new Exception(e);
        }
    }
}
