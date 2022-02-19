package com.webAnalytic.Services;

import com.webAnalytic.Auxiliary.Hash;
import com.webAnalytic.Auxiliary.Config.Security.UserRole;
import com.webAnalytic.Domains.DAO.AccessUserWebSiteDAO;
import com.webAnalytic.Domains.DAO.DAO;
import com.webAnalytic.Domains.DAO.UserDAO;
import com.webAnalytic.Domains.User;
import com.webAnalytic.Domains.WebSite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserDAO userDAO;
    private final DAO<WebSite> webSiteDAO;
    private final AccessUserWebSiteDAO accessWebSiteServiceDAO;

    @Autowired
    public UserService(UserDAO userDAO, DAO<WebSite> webSiteDAO, AccessUserWebSiteDAO accessWebSiteServiceDAO) {
        this.userDAO = userDAO;
        this.webSiteDAO = webSiteDAO;
        this.accessWebSiteServiceDAO = accessWebSiteServiceDAO;
    }

    /**
     * Returns true if the user is the owner of the website (he added it), otherwise false;
     *
     * @param userId - id of user;
     * @param siteId - id of site;
     */
    public boolean isOwnerWebSite(long userId, long siteId) throws Exception {
        User ownerUser = webSiteDAO.getById(siteId).getAdmin();
        return userId == ownerUser.getId();
    }

    /**
     * Returns true if the user is the owner of the user (he added it), otherwise false;
     *
     * @param parentUserId - id of user (parent);
     * @param childUserId  - id of user (child);
     */
    public boolean isOwnerUser(long parentUserId, long childUserId) {
        User childUser = userDAO.getById(childUserId);
        return parentUserId == childUser.getUserAdminId();
    }

    /**
     * Returns true if user has access to the data; otherwise false.
     *
     * @param webSiteId - id of webSiteId;
     * @param userId  - id of user.
     * */
    public boolean hasAccess(long webSiteId, long userId) throws Exception {
        if (isOwnerWebSite(userId, webSiteId))
            return true;
        return accessWebSiteServiceDAO.userHasAccess(webSiteId,userId);
    }

    public boolean create(User user, UserRole role)
            throws Exception {

        // Hashing password
        var hashPassword = Hash.doHash(user.getPasswordString());

        // Set other params
        user.setPassword(hashPassword);
        user.setAdminState(role == UserRole.ADMIN);

        return userDAO.create(user);
    }

    public boolean updateMySelf(long updateUserId, String newName, String newPassword) throws Exception {
        User userUpdate = userDAO.getById(updateUserId);

        // Update fields
        if (newName != null && !newName.isEmpty())
            userUpdate.setName(newName);

        if (newPassword != null && !newPassword.isEmpty())
            userUpdate.setPassword(Hash.doHash(newPassword));

        return userDAO.update(userUpdate);
    }

    public boolean updateUser(long ownerUserId, long updateUserId, String newName, String newPassword)
            throws Exception {
        // Check access
        if (!isOwnerUser(ownerUserId, updateUserId))
            return false;

        User userUpdate = userDAO.getById(updateUserId);

        // Update fields
        if (newName != null && !newName.isEmpty())
            userUpdate.setName(newName);

        if (newPassword != null && !newPassword.isEmpty())
            userUpdate.setPassword(Hash.doHash(newPassword));

        return userDAO.update(userUpdate);
    }

    public boolean deleteMySelf(long mySelfUserId) {
        return userDAO.deleteById(mySelfUserId);
    }

    public boolean deleteByID(long ownerUserId, long deleteUserId) {
        if (!isOwnerUser(ownerUserId,deleteUserId))
            return false;

        return userDAO.deleteById(deleteUserId);
    }

    public List<User> getUsersList(User user) {
        return userDAO.getUsersByAdmin(user.getId());
    }

}