package com.webAnalytic.Controllers;

import com.webAnalytic.Services.CollectorService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("collector")
public class CollectorController extends BaseController {

    @Autowired
    private final CollectorService visitService;

    public CollectorController(CollectorService visitService) {
        this.visitService = visitService;
    }

    /**
     * Accepts request from visitor and registered in database;
     * This method allows cross-domain communication;
     *
     * @param infoJSONObject - contain info about visit;
     * @param request        - object {@link HttpServletRequest}
     */
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Long> visit(@RequestBody String infoJSONObject,
                                      HttpServletRequest request) throws Exception {
        JSONObject jsonObject = new JSONObject(infoJSONObject);

        String userAgent = request.getHeader("User-Agent");

        Long inUid = (jsonObject.has("uid") ? Long.parseLong(jsonObject.getString("uid")) : 0L);

        var out = visitService.addVisit(inUid,
                jsonObject.getLong("siteId"),
                userAgent,
                request.getRemoteAddr(),
                jsonObject.getString("ref"),
                jsonObject.getString("page"),
                jsonObject.getString("scr"));
        if (out == -1) // if -1 then this is an error
            return ResponseEntity.badRequest().build();

        if (out > 0) // if > - added new visitor
            return new ResponseEntity<>(out, HttpStatus.CREATED);

        return ResponseEntity.ok().build();
    }
}