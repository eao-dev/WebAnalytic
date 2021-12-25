package com.service.OnlineStat.Controllers;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;

class UsersTime extends HashMap<Long, java.util.Date> {
    public UsersTime() {
        super();
    }
}

@RestController
@RequestMapping("/")
public class MainController {

    /**
     * The difference in seconds between requests, indicating that the user is online;
     */
    public static final long secondOnline = 10;

    public HashMap<Long, UsersTime> mapSite = new HashMap<>();

    @Value("${accessToken}")
    public String validAccessToken;

    /**
     * Check access using access token;
     *
     * @param accessToken - access token string;
     * @return void if access token is valid, otherwise throw exception (ForbiddenException).
     */
    boolean checkAccess(String accessToken) {
        return validAccessToken.equals(accessToken);
    }

    /**
     * Calculate online visitors for web-site;
     *
     * @param siteId - id of web-site;
     * @return - count of online-visitors.
     */
    long calcOnlineStat(long siteId) {
        var usersTime = mapSite.get(siteId);
        var currentDateTime = new Date();

        long count = 0;

        for (var time : usersTime.values()) {
            long deltaMs = currentDateTime.getTime() - time.getTime();

            if (deltaMs <= (1000 * secondOnline))
                ++count;
        }

        return count;
    }

    /**
     * Returns count of online-visitor on web-site
     *
     * @param accessToken - access token string
     * @param siteId      - id of web-site
     */
    @GetMapping("info")
    @CrossOrigin(origins="*")
    public ResponseEntity<Long> info(@RequestParam(name="accessToken") String accessToken,
                                     @RequestParam(name = "siteId") long siteId) {
        if (!checkAccess(accessToken))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Long result = calcOnlineStat(siteId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Add new web-site
     */
    @RequestMapping(value = "addSites", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity addSites(@RequestBody String json) {

        JSONObject jsonObject = new JSONObject(json);
        var siteArray = jsonObject.getJSONArray("sites");

        if (!checkAccess(jsonObject.getString("accessToken")))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        for (int i = 0; i < siteArray.length(); ++i) {
            var siteId = siteArray.getLong(i);
            if (mapSite.get(siteId) == null)
                mapSite.put(siteId, new UsersTime());
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Delete web-site.
     */
    @DeleteMapping("deleteSite")
    public ResponseEntity deleteSite(@RequestParam(name="accessToken") String accessToken,
                                     @RequestParam(name = "siteId") long siteId) {

        if (!checkAccess(accessToken))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        mapSite.remove(siteId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Accepts requests from the visitor that he is still online.
     */
    @RequestMapping(value = "ping", method = RequestMethod.POST, produces = "application/json")
    @CrossOrigin(origins="*")
    public ResponseEntity online(@RequestBody String json) {
        JSONObject jsonObject = new JSONObject(json);

        var mapUsersTime = mapSite.get(jsonObject.getLong("siteId"));

        if (mapUsersTime != null) // If site found
            mapUsersTime.put(jsonObject.getLong("visitorId"), new Date()); // Update time
        else
            new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Site not found

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}