package com.webAnalytic.Controllers;

import com.webAnalytic.Services.CollectorService;
import org.apache.coyote.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("collector")
public class CollectorController extends BaseController {

    @Autowired
    private final CollectorService visitService;

    public CollectorController(CollectorService visitService) {
        this.visitService = visitService;
    }

    /**
     * Accepts request from visitor and registered in data base;
     * This method allows cross-domain communication;
     *
     * @param uid              - unique ID from cookie value;
     * @param infoJSONObject   - contain info about visit;
     * @param request          - object {@link HttpServletRequest}
     * @param response         - object {@link HttpServletResponse}
     */
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> visit(@CookieValue(value = "uid", defaultValue = "") String uid,
                                      @RequestBody String infoJSONObject,
                                      HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Set headers
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add("Access-Control-Allow-Origin", request.getRemoteHost());
//        httpHeaders.add("Access-Control-Allow-Methods", "POST");
//        httpHeaders.add("Access-Control-Allow-headers", "content-type");

        JSONObject jsonObject = new JSONObject(infoJSONObject);

        String userAgent = request.getHeader("User-Agent");
        if (!visitService.addVisit(uid,
                jsonObject.getLong("siteId"),
                userAgent,
                request.getRemoteAddr(),
                jsonObject.getString("ref"),
                jsonObject.getString("page"),
                jsonObject.getString("scr"),response)){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }
}