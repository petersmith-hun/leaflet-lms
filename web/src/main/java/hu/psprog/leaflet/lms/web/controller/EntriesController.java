package hu.psprog.leaflet.lms.web.controller;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.service.EntryBridgeService;
import hu.psprog.leaflet.lms.web.response.handler.impl.EntryListDataExtractor;
import hu.psprog.leaflet.lms.web.response.model.entry.EntryResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * @author Peter Smith
 */
@Controller
@RequestMapping("/entries")
public class EntriesController {

    @Autowired
    private EntryBridgeService entryBridgeService;

    @Autowired
    private EntryListDataExtractor entryListDataExtractor;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getAllEntries() throws Exception {

        try {
            Map<String, Object> response = entryBridgeService.getAllEntries();
            List<EntryResponseModel> entryResponseModelList = entryListDataExtractor.extract(response);
            ModelAndView modelAndView = new ModelAndView("view/entries/list");
            modelAndView.addObject("entries", entryResponseModelList);

            return modelAndView;
        } catch (CommunicationFailureException e) {
            throw new Exception(e);
        }
    }
}
