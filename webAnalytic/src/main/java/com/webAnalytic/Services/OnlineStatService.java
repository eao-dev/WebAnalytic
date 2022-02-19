package com.webAnalytic.Services;

import com.webAnalytic.Domains.DAO.WebSiteDAO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service for interacting with the server OnlineStat.
 * */

@Service
public class OnlineStatService {

    @Value("${hostOnlineServerAddress}")
    private String address;

    @Value("${accessTokenOnlineStat}")
    private String accessToken;

    private final static String addSitesPath = "/addSites";
    private final static String onlineInfoPath = "/info";
    private final static String deleteSitePath = "/deleteSite";

    private final RestTemplate restTemplate;

    @Autowired
    private final WebSiteDAO webSiteDAO;

    public OnlineStatService(RestTemplateBuilder restTemplateBuilder, WebSiteDAO webSiteDAO) {
        this.restTemplate = restTemplateBuilder.build();
        this.webSiteDAO = webSiteDAO;
    }

    /**
     * Send a complete list of sites to OnlineStat-server;
     *
     * This method call at start.
     * */
    @PostConstruct
    private void sendInfoAboutSites() {
        System.out.println("Send info about websites to online stat service");

        var siteList = webSiteDAO.list();
        List<Long> sites = new ArrayList<>();
        for (var site : siteList)
            sites.add(site.getId());
        addSites(sites);
    }

    /**
     * Send siteId to onlineStat-service.
     *
     * @param siteId - id of web-site;
     * */
    boolean addSite(long siteId) {
        var object = new ArrayList<Long>();
        object.add(siteId);
        return addSites(object);
    }

    /**
     * Remove site from onlineStat-server;
     *
     * @param siteId - id of web-site.
     * */
    public void deleteSite(long siteId) {
        try {
            String addSiteUrl = address + deleteSitePath;
            String url = UriComponentsBuilder.fromHttpUrl(addSiteUrl)
                    .queryParam("siteId", siteId)
                    .queryParam("accessToken", accessToken)
                    .encode()
                    .toUriString();
            this.restTemplate.delete(url);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send sites to OnlineStat-server;
     *
     * @param sites - List contains id of websites.
     **/
    boolean addSites(List<Long> sites) {
        try {
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // Create send data
            JSONArray jsonArray = new JSONArray();
            var siteList = webSiteDAO.list();
            for (var site : sites)
                jsonArray.put(site);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("accessToken", accessToken);
            jsonObject.put("sites", jsonArray);

            // send POST request
            HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);
            String addSiteUrl = address + addSitesPath;
            ResponseEntity<String> response = this.restTemplate.postForEntity(addSiteUrl, entity, String.class);

            // Created

            return response.getStatusCode() == HttpStatus.CREATED;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Returns the number of online visitors for specified site;
     *
     * @param siteId - id of web-site.
     */
    public long getOnlineVisitors(long siteId) {
        try {
            System.out.println("Try get count of online visitors");

            String infoSiteUrl = address + onlineInfoPath;
            String url = UriComponentsBuilder.fromHttpUrl(infoSiteUrl)
                    .queryParam("siteId", siteId)
                    .queryParam("accessToken", accessToken)
                    .encode()
                    .toUriString();

            // Send GET request
            var result = restTemplate.getForObject(url, Long.class);
            if (result == null)
                return 0;
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

}
