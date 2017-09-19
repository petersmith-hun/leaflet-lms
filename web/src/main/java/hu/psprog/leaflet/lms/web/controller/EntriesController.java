package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.api.rest.response.entry.EntryListDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.EntryBridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Peter Smith
 */
@Controller
@RequestMapping("/entries")
public class EntriesController {

    @Autowired
    private EntryBridgeService entryBridgeService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getAllEntries() throws CommunicationFailureException {

        EntryListDataModel response = entryBridgeService.getAllEntries();
        ModelAndView modelAndView = new ModelAndView("view/entries/list");
        modelAndView.addObject("entries", response.getEntries());

        return modelAndView;
    }
}
