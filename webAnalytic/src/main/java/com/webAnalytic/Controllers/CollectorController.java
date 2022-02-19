package com.webAnalytic.Controllers;

import com.webAnalytic.Services.CollectorService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * This controller manages the collection of visitor data and also adds new visitors.
 * */

@RestController
@RequestMapping("collector")
public class CollectorController extends BaseController {

    @Autowired
    private final CollectorService visitService;

    public CollectorController(CollectorService visitService) {
        this.visitService = visitService;
    }

    /**
     * Accepts request from visitor and registered in DB;
     * This method allows cross-domain communication;
     *
     * @param infoJSON - contain info about visit;
     * @param request        - object {@link HttpServletRequest}
     */
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Long> visit(@RequestBody String infoJSON,
                                      HttpServletRequest request) throws Exception {

        JSONObject visitorInfo = new JSONObject(infoJSON);
        long visitorUid = (visitorInfo.has("uid") ? Long.parseLong(visitorInfo.getString("uid")) : 0L);

        var out = visitService.addVisit(visitorUid,
                visitorInfo.getLong("siteId"),
                request.getHeader("User-Agent"),
                request.getRemoteAddr(),
                visitorInfo.getString("ref"),
                visitorInfo.getString("page"),
                visitorInfo.getString("scr"));

        if (out == -1) // if -1 then this is an error
            return ResponseEntity.ok().build();

        if (out > 0) // if > - added new visitor
            return new ResponseEntity<>(out, HttpStatus.CREATED);

        return ResponseEntity.ok().build();
    }
}