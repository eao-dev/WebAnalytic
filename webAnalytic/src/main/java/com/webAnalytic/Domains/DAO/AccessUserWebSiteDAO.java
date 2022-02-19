package com.webAnalytic.Domains.DAO;

import com.webAnalytic.Auxiliary.JDBCLayer;
import com.webAnalytic.Domains.AccessUserWebSite;
import com.webAnalytic.Domains.IMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.List;

@Component
public class AccessUserWebSiteDAO implements DAO<AccessUserWebSite> {

    private final JDBCLayer jdbcLayer;
    private final IMapper<AccessUserWebSite> accessUserWebSiteMapper;

    @Autowired
    public AccessUserWebSiteDAO(JDBCLayer jdbcLayer,IMapper<AccessUserWebSite> accessUserWebSiteMapper) {
        this.jdbcLayer = jdbcLayer;
        this.accessUserWebSiteMapper = accessUserWebSiteMapper;
    }

    public List<AccessUserWebSite> getAccessListByAdmin(long adminId, long webSiteId) {
        assert (adminId > 0);
        assert (webSiteId > 0);

        String sqlQuery = "select AdminUser.ID as AdminUserId, \n" +
                "AdminUser.Login as AdminUserLogin,\n" +
                "AdminUser.Password as AdminUserPassword,\n" +
                "AdminUser.Name as AdminUserName,\n" +
                "AdminUser.isAdmin as AdminUserIsAdmin,\n" +
                "SimplyUser.id as User_id,\n" +
                "SimplyUser.login as UserLogin,\n" +
                "SimplyUser.password as UserPassword,\n" +
                "SimplyUser.name as UserName,\n" +
                "SimplyUser.UserAdmin_id as UserUserAdmin_id,\n" +
                "*\n" +
                " from [User] as AdminUser\n" +
                " inner join [User] as SimplyUser on SimplyUser.UserAdmin_id = AdminUser.ID\n" +
                " left join [AccessUserWebSite] on [AccessUserWebSite].User_id = SimplyUser.ID and " +
                "[AccessUserWebSite].WebSite_id = ?\n" +
                " left join [WebSite] on [WebSite].ID = [AccessUserWebSite].WebSite_id\n" +
                " where AdminUser.id = ? and (WebSite_id is null or WebSite_id = ?)";

        return jdbcLayer.select(sqlQuery, accessUserWebSiteMapper, webSiteId, adminId, webSiteId);
    }

    public List<AccessUserWebSite> getAccessListByUser(Long userId) {
        assert (userId > 0);

        String sqlQuery = "select AdminUser.ID as AdminUserId, \n" +
                "AdminUser.Login as AdminUserLogin,\n" +
                "AdminUser.Password as AdminUserPassword,\n" +
                "AdminUser.Name as AdminUserName,\n" +
                "AdminUser.isAdmin as AdminUserIsAdmin,\n" +
                "SimplyUser.id as User_id,\n" +
                "SimplyUser.login as UserLogin,\n" +
                "SimplyUser.password as UserPassword,\n" +
                "SimplyUser.name as UserName,\n" +
                "SimplyUser.UserAdmin_id as UserUserAdmin_id,\n" +
                "*\n" +
                " from [User] as AdminUser\n" +
                " inner join [User] as SimplyUser on SimplyUser.UserAdmin_id = AdminUser.ID\n" +
                " left join [AccessUserWebSite] on [AccessUserWebSite].User_id = SimplyUser.ID and " +
                "[AccessUserWebSite].WebSite_id = ?\n" +
                " left join [WebSite] on [WebSite].ID = [AccessUserWebSite].WebSite_id\n" +
                " [User].ID = ?";

        return jdbcLayer.select(sqlQuery, accessUserWebSiteMapper, userId);
    }

    @Override
    public boolean create(AccessUserWebSite accessUserWebSite) {
        assert (accessUserWebSite != null);
        String sqlQuery = "insert into [AccessUserWebSite] (WebSite_id, User_id) values (?,?)";
        return jdbcLayer.update(sqlQuery, accessUserWebSite.getWebSite().getId(),
                accessUserWebSite.getUser().getId()) == 1;
    }

    @Override
    public boolean deleteByObject(AccessUserWebSite accessUserWebSite) {
        assert (accessUserWebSite != null);
        String sqlQuery = "delete from [AccessUserWebSite] where (WebSite_id=?) and (User_id=?)";
        return jdbcLayer.update(sqlQuery, accessUserWebSite.getWebSite().getId(),
                accessUserWebSite.getUser().getId()) == 1;
    }

    public boolean userHasAccess(long webSiteId, long userId) {
        assert (webSiteId != 0);
        assert (userId != 0);
        String sqlQuery = "select * from [AccessUserWebSite]  where (WebSite_id=?) and (User_id=?)";
        return jdbcLayer.select(sqlQuery, ResultSet::first, webSiteId, userId).stream().findFirst().orElse(false);
    }


}
