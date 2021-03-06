package com.webAnalytic.Services;

import com.webAnalytic.Domains.DAO.AccessUserWebSiteDAO;
import com.webAnalytic.Domains.AccessUserWebSite;
import com.webAnalytic.Domains.User;
import com.webAnalytic.Domains.WebSite;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessWebSiteService {

    private final AccessUserWebSiteDAO accessUserWebSiteDAO;
    private final UserService userService;

    @Autowired
    public AccessWebSiteService(AccessUserWebSiteDAO accessUserWebSiteDAO, UserService userService) {
        this.accessUserWebSiteDAO = accessUserWebSiteDAO;
        this.userService = userService;
    }

    /**
     * Allows the user to access the site;
     *
     * @param userId - id of user;
     * @param siteId - id of web-site;
     * @return true if success, otherwise false.
     */
    public boolean setAccess(long ownerUserId, long userId, long siteId) throws Exception {

        if (!userService.isOwnerWebSite(ownerUserId, siteId) ||
                !userService.isOwnerUser(ownerUserId, userId)
        ) return false;

        AccessUserWebSite newAccessUserWebSite = new AccessUserWebSite();
        newAccessUserWebSite.setUser(new User(userId));
        newAccessUserWebSite.setWebSite(new WebSite(siteId));
        return accessUserWebSiteDAO.create(newAccessUserWebSite);
    }

    /**
     * Denies the user access to the site;
     *
     * @param userId - id of user;
     * @param siteId - id of web-site;
     * @return true if success, otherwise false.
     */
    public boolean deleteAccess(long ownerUserId, long userId, long siteId) throws Exception {
        if (!userService.isOwnerWebSite(ownerUserId, siteId) ||
                !userService.isOwnerUser(ownerUserId, userId)
        ) return false;
        AccessUserWebSite newAccessUserWebSite = new AccessUserWebSite();
        newAccessUserWebSite.setUser(new User(userId));
        newAccessUserWebSite.setWebSite(new WebSite(siteId));
        return accessUserWebSiteDAO.deleteByObject(newAccessUserWebSite);
    }

    /**
     * Returns list of users with permissions for web-site;
     *
     * @param admin     - the administrator whose users will be returned;
     * @param webSiteId - id of web-site;
     * @return JSON-array contain user list with permission.
     */
    public JSONObject usersWithPermissions(User admin, long webSiteId) throws Exception {
        if (!userService.isOwnerWebSite(admin.getId(), webSiteId))
            return null;
        var listAccessUserWebSite = accessUserWebSiteDAO.getAccessListByAdmin(admin.getId(), webSiteId);
        JSONArray jsonArray = new JSONArray();

        if (listAccessUserWebSite != null) {
            for (var access : listAccessUserWebSite) {
                var user = access.getUser();
                var webSite = access.getWebSite();

                JSONObject userJson = new JSONObject();
                userJson.put("name", user.getName());
                userJson.put("id", user.getId());

                var userPermission = new JSONObject().
                        put("user", userJson).
                        put("isAllow", webSite.getId() == webSiteId);

                jsonArray.put(userPermission);
            }
        }

        var jsonObjectOut = new JSONObject();
        jsonObjectOut.put("userListPermission", jsonArray);
        return jsonObjectOut;
    }


}
