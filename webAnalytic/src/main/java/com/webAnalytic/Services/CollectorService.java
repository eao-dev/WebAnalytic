package com.webAnalytic.Services;

import com.maxmind.geoip2.DatabaseReader;
import com.webAnalytic.DAO.DAO;
import com.webAnalytic.DAO.VisitorDAO;
import com.webAnalytic.Entity.Resource;
import com.webAnalytic.Entity.Visit;
import com.webAnalytic.Entity.Visitor;
import com.webAnalytic.Entity.WebSite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import ua_parser.Parser;

import javax.annotation.PreDestroy;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Base64;

@Service
public class CollectorService {

    @Autowired
    private final DAO<Resource> resourceDAO;

    @Autowired
    private final VisitorDAO visitorDAO;

    @Autowired
    private final DAO<Visit> visitDAO;

    @Autowired
    private final DAO<WebSite> webSiteDAO;

    //    @Value("${dbGeoIP}")
    private String dbGeoIP = "GeoLite2-country.mmdb"; // todo: fix it => spring не находит значение.

    private final File database = new File(dbGeoIP);
    private final DatabaseReader dbReader = new DatabaseReader.Builder(database).build();

    public CollectorService(DAO<Resource> resourceDAO, VisitorDAO visitorDAO, DAO<Visit> visitDAO,
                            DAO<WebSite> webSiteDAO) throws IOException {
        this.resourceDAO = resourceDAO;
        this.visitorDAO = visitorDAO;
        this.visitDAO = visitDAO;
        this.webSiteDAO = webSiteDAO;
    }

    @PreDestroy
    private void destroy() throws IOException {
        dbReader.close();
    }

    /**
     * Return country-code for IP
     *
     * @param ip - IP-address
     */
    private String getCountry(String ip) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            return dbReader.country(ipAddress).getCountry().getIsoCode();
        } catch (Exception ex) {
            return "-";
        }
    }

    /**
     * Add visit to DB;
     *
     * @param uid       - unique ID from cookie of visitor;
     * @param webSiteId - if of web-site;
     * @param userAgent - HTTP-header field: user-agent;
     * @param ip        - IP-address of visitor;
     * @param referer   - HTTP-header field: referer;
     * @param page      - visited page;
     * @param scr       - screen resolution;
     */
    public boolean addVisit(String uid, long webSiteId, String userAgent, String ip,
                            String referer, String page, String scr, HttpServletResponse response) throws Exception {

        // Get visitor
        Visitor visitor;
        if (!uid.isEmpty()) {
            visitor = visitorDAO.getById(Long.parseLong(uid));
            if (visitor == null)
                return false;
        } else { // This is new visitor. Add to DB
            visitor = new Visitor();

            var clientInfo = new Parser().parse(userAgent);

            visitor.setIp(ip.getBytes());
            visitor.setCountry(getCountry(ip));

            visitor.setScResolution(new String(Base64.getDecoder().decode(scr)));

            visitor.setOS(clientInfo.os.family);
            visitor.setBrowser(clientInfo.userAgent.family);

            String device = clientInfo.device.family;
            if (device.equals("Other"))
                device = "PC";

            visitor.setDevice(device);

            // Add new visitor to DB
            if (!visitorDAO.createWithLastInsertedId(visitor)) // here the identifier will be assigned to the object
                return false;

            // Set cookie
            var cookie = new Cookie("uid", Long.toString(visitor.getId()));
            cookie.setPath("/");
            cookie.setSecure(false);
            response.addCookie(cookie);
            response.addHeader("Access-Control-Allow-Credentials", "true");
        }

        // Add new visit to DB

        // Set referer
        String refererDecoded = "";
        if (!referer.isEmpty())
            refererDecoded = new String(Base64.getDecoder().decode(referer));

        // Set visited page
        String pageDecoded = "";
        if (!page.isEmpty())
            pageDecoded = new String(Base64.getDecoder().decode(page));

        // Find domain
        WebSite webSite = webSiteDAO.getById(webSiteId);

        if (webSite == null)
            return false;

        // Create target resource
        Resource resource = resourceDAO.getByObject(new Resource(webSite, pageDecoded));
        if (resource == null) {
            resource = new Resource(webSite, pageDecoded);
            // Add target resource
            if (!resourceDAO.create(resource))
                return false;
        }

        // Create visit
        Visit visit = new Visit(refererDecoded, resource, visitor);

        // Add visit
        return visitDAO.create(visit);

    }
}