package com.webAnalytic.Services;

import com.webAnalytic.DAO.AccessUserWebSiteDAO;
import com.webAnalytic.DAO.WebSiteDAO;
import com.webAnalytic.Entity.User;
import com.webAnalytic.Entity.WebSite;
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
        boolean addResult = webSiteDAO.createWithLastInsertedId(webSite); // TODO: set comment

        if (addResult) {
            if (!onlineStatService.addSite(webSite.getId())) {
                // todo: log error-message
            }
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
     *
     */
    public List<WebSite> getWebSiteList(User userAuth) throws Exception {

        if (userAuth.isAdmin())
            return webSiteDAO.listByObject(userAuth);
        else {
            List<WebSite> result = new ArrayList<>();
            for (var it : accessUserWebSiteDAO.listBySimplyUser(userAuth))
                result.add(it.getWebSite());

            return result;
        }

    }
}
