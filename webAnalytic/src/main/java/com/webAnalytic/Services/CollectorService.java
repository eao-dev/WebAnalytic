package com.webAnalytic.Services;

import com.maxmind.geoip2.DatabaseReader;
import com.webAnalytic.Domains.DAO.DAO;
import com.webAnalytic.Domains.DAO.VisitorDAO;
import com.webAnalytic.Domains.Resource;
import com.webAnalytic.Domains.Visit;
import com.webAnalytic.Domains.Visitor;
import com.webAnalytic.Domains.WebSite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua_parser.Parser;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Base64;

@Service
public class CollectorService {

    private final DAO<Resource> resourceDAO;

    private final VisitorDAO visitorDAO;

    private final DAO<Visit> visitDAO;

    private final DAO<WebSite> webSiteDAO;

    private DatabaseReader dbReader;

    @Value("${dbGeoIP}")
    private String dbGeoIP;

    @Autowired
    public CollectorService(DAO<Resource> resourceDAO, VisitorDAO visitorDAO, DAO<Visit> visitDAO,
                            DAO<WebSite> webSiteDAO) {
        this.resourceDAO = resourceDAO;
        this.visitorDAO = visitorDAO;
        this.visitDAO = visitDAO;
        this.webSiteDAO = webSiteDAO;
    }

    @PostConstruct
    void connectToGeoIpDb() throws IOException {
        File database = new File(dbGeoIP);
        dbReader = new DatabaseReader.Builder(database).build();
    }

    @PreDestroy
    private void destroy() throws IOException {
        dbReader.close();
    }

    /**
     * Return country-code by IP;
     *
     * @param ip - IP-address.
     */
    private String getCountryByIp(String ip) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            return dbReader.country(ipAddress).getCountry().getIsoCode();
        } catch (Exception ex) {
            return "-";
        }
    }

    /**
     * Create new visitor with user-agent, IP and screen-resolution.
     */
    Visitor createNewVisitor(String userAgent, String IP, String screenResolution) {
        Visitor visitor = new Visitor();

        var clientInfo = new Parser().parse(userAgent);

        visitor.setCountry(getCountryByIp(IP));
        visitor.setScResolution(new String(Base64.getDecoder().decode(screenResolution)));
        visitor.setOS(clientInfo.os.family);
        visitor.setBrowser(clientInfo.userAgent.family);

        String device = clientInfo.device.family;
        if (device.equals("Other"))
            device = "PC";

        visitor.setDevice(device);

        // Add new visitor to DB
        if (!visitorDAO.createWithLastInsertedId(visitor)) // here the identifier will be assigned to the object
            return null;

        return visitor;
    }

    /**
     * Add visit to DB;
     *
     * @param uid       - unique ID from cookie of visitor;
     * @param webSiteId - if of web-site;
     * @param userAgent - HTTP-header field: user-agent;
     * @param IP        - IP-address of visitor;
     * @param referer   - HTTP-header field: referer;
     * @param page      - visited page;
     * @param scr       - screen resolution;
     */
    public long addVisit(final long uid, final long webSiteId, final String userAgent, final String IP,
                         final String referer, final String page, final String scr) throws Exception {
        long ret = 0;

        // Get visitor
        Visitor visitor = visitorDAO.getById(uid);

        if (visitor == null) { // This is new visitor. Add to DB
            visitor = createNewVisitor(userAgent, IP, scr);
            if (visitor == null)
                return -1;

            // UID for new user
            ret = visitor.getId();
        }

        // Find website
        WebSite webSite = webSiteDAO.getById(webSiteId);
        if (webSite == null)
            return -1;

        var decoderB64 = Base64.getDecoder();

        // Set referer
        String refererDecoded = null;
        if (!referer.isEmpty())
            refererDecoded = new String(decoderB64.decode(referer));

        // Set visited page
        String pageDecoded = "";
        if (!page.isEmpty())
            pageDecoded = new String(decoderB64.decode(page));

        // Find resource
        Resource resource = resourceDAO.getByObject(new Resource(webSite, pageDecoded));
        if (resource == null) {
            resource = new Resource(webSite, pageDecoded);

            // Add resource
            if (!resourceDAO.create(resource))
                return -1;
        }

        // Add visit
        Visit visit = new Visit(refererDecoded, resource, visitor);
        if (!visitDAO.create(visit))
            return -1;

        return ret;
    }
}