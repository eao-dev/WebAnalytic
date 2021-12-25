package com.webAnalytic.Entity.Mapper.AccessUserWebSite;

import com.webAnalytic.Entity.AccessUserWebSite;
import com.webAnalytic.Entity.Mapper.IMapper;
import com.webAnalytic.Entity.User;
import com.webAnalytic.Entity.WebSite;
import org.springframework.stereotype.Component;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AccessUserWebSiteMapper implements IMapper<AccessUserWebSite> {

    @Override
    public AccessUserWebSite map(ResultSet resultSet) throws SQLException {

        WebSite webSite = new WebSite();
        webSite.setId(resultSet.getLong("webSite_id"));
        webSite.setDomain(resultSet.getString("domain"));
        webSite.setDate(resultSet.getDate("DateTime"));

        User user = new User();
        user.setId(resultSet.getLong("User_id"));
        user.setLogin(resultSet.getString("Login"));
        user.setName(resultSet.getString("Name"));
        user.setPassword(resultSet.getBytes("password"));
        user.setUserAdminId(resultSet.getLong("Admin_id"));
        user.setAdminState(resultSet.getBoolean("isAdmin"));

        return new AccessUserWebSite(webSite, user);
    }
}
