package com.webAnalytic.Entity.Mapper.AccessUserWebSite;

import com.webAnalytic.Entity.AccessUserWebSite;
import com.webAnalytic.Entity.Mapper.IMapper;
import com.webAnalytic.Entity.User;
import com.webAnalytic.Entity.WebSite;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AccessUserWebSiteJoinMapper implements IMapper<AccessUserWebSite> {

    @Override
    public AccessUserWebSite map(ResultSet resultSet) throws SQLException {

        User user = new User(
                resultSet.getLong("User_id"),
                resultSet.getString("UserLogin"),
                resultSet.getBytes("UserPassword"),
                resultSet.getString("UserName"),
                false,/*resultSet.getBoolean("UserIsAdmin")*/
                resultSet.getLong("UserUserAdmin_id"));

        User admin = new User(
                resultSet.getLong("AdminUserId"),
                resultSet.getString("AdminUserLogin"),
                resultSet.getBytes("AdminUserPassword"),
                resultSet.getString("AdminUserName"),
                resultSet.getBoolean("AdminUserIsAdmin"),
                0);

        WebSite webSite = new WebSite(resultSet.getLong("WebSite_id"),
            resultSet.getString("Domain"), admin);

        return new AccessUserWebSite(webSite, user);
    }
}
