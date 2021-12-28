package com.webAnalytic.DAO;

import com.webAnalytic.Entity.AccessUserWebSite;
import com.webAnalytic.Entity.Mapper.IMapper;
import com.webAnalytic.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.List;

@Component
public class AccessUserWebSiteDAO implements DAO<AccessUserWebSite> {

    private final JDBCLayer jdbcLayer;
    private final IMapper<AccessUserWebSite> accessUserWebSiteMapper;
    private final IMapper<AccessUserWebSite> accessUserWebSiteJoinMapper;

    @Autowired
    public AccessUserWebSiteDAO(JDBCLayer jdbcLayer,
                                IMapper<AccessUserWebSite> accessUserWebSiteMapper,
                                @Qualifier("accessUserWebSiteJoinMapper")
                                        IMapper<AccessUserWebSite> accessUserWebSiteJoinMapper
    ) {
        this.jdbcLayer = jdbcLayer;
        this.accessUserWebSiteMapper = accessUserWebSiteMapper;
        this.accessUserWebSiteJoinMapper = accessUserWebSiteJoinMapper;
    }

//    @Override
    public List<AccessUserWebSite> allUserListWithAccessByAdminId(long adminId, long webSiteId) {

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

        return jdbcLayer.select(sqlQuery, accessUserWebSiteJoinMapper, webSiteId, adminId, webSiteId);
    }

    public List<AccessUserWebSite> listBySimplyUser(Object user) {
        assert (user != null);

        String sqlQuery = "select [User].id as User_id, [WebSite].ID as WebSite_id, * from [AccessUserWebSite] \n" +
                "inner join [User] on [User].ID = [AccessUserWebSite].User_id\n" +
                "inner join [WebSite] on [WebSite].ID = [AccessUserWebSite].WebSite_id where [User].ID = ?";

        return jdbcLayer.select(sqlQuery, accessUserWebSiteMapper, ((User)user).getId());
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

        IMapper<Boolean> mapper = (ResultSet resultSet) -> {
            try {
                resultSet.getLong("webSite_id");
            } catch (Exception ex){
                return false;
            }
            return true;
        };

        String sqlQuery = "select * from [AccessUserWebSite]  where (WebSite_id=?) and (User_id=?)";
        return jdbcLayer.select(sqlQuery, mapper, webSiteId, userId).stream().findFirst().orElse(false);
    }


}
