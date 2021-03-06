package com.webAnalytic.Services;

import com.webAnalytic.Domains.DAO.AccessUserWebSiteDAO;
import com.webAnalytic.Domains.DAO.WebSiteDAO;
import com.webAnalytic.Domains.User;
import com.webAnalytic.Domains.WebSite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WebSiteService {

    private final WebSiteDAO webSiteDAO;
    private final OnlineStatService onlineStatService;
    private final AccessUserWebSiteDAO accessUserWebSiteDAO;
    private final UserService userService;

    @Autowired
    public WebSiteService(WebSiteDAO webSiteDAO, OnlineStatService onlineStatService,
                          AccessUserWebSiteDAO accessUserWebSiteDAO, UserService userService) {
        this.webSiteDAO = webSiteDAO;
        this.onlineStatService = onlineStatService;
        this.accessUserWebSiteDAO = accessUserWebSiteDAO;
        this.userService = userService;
    }

    /**
     * Create new web-site {@link WebSite} in DB;
     *
     * @param webSite - web-site object @link WebSite};
     * @param admin   - administrator of web-site;
     */
    public boolean create(WebSite webSite, User admin) throws Exception {
        webSite.setAdmin(admin);
        boolean addResult = webSiteDAO.createWithLastInsertedId(webSite);

        if (addResult) {
            if (!onlineStatService.addSite(webSite.getId()))
                System.out.println("Error: not added web-site to online-stat!");
        }

        return addResult;
    }

    /**
     * Remove web-site from DB use id of web-site; If success return true, otherwise false;
     *
     * @param webSiteId - id of web-site;
     */
    public boolean delete(long ownerSiteId, long webSiteId) throws Exception {

        if (!userService.isOwnerWebSite(ownerSiteId, webSiteId))
            return false;

        // Send info to onlineStat-service
        onlineStatService.deleteSite(webSiteId);
        return webSiteDAO.deleteById(webSiteId);
    }

    /**
     * Returns web-site object {@link WebSite} use id of web-site;
     *
     * @param webSiteId - id of web-site;
     */
    public WebSite getById(long webSiteId) throws Exception {
        return webSiteDAO.getById(webSiteId);
    }

    /**
     * Returns a list of sites for specified user.
     *
     * @param userAuth - user for which the list of sites will be generated.
     */
    public List<WebSite> getWebSiteList(User userAuth) {

        if (userAuth.isAdmin())
            return webSiteDAO.listByAdmin(userAuth.getId());
        else {
            List<WebSite> result = new ArrayList<>();
            for (var it : accessUserWebSiteDAO.getAccessListByUser(userAuth.getId()))
                result.add(it.getWebSite());

            return result;
        }

    }
}
