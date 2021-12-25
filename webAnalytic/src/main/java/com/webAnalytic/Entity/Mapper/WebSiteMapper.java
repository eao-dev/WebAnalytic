package com.webAnalytic.Entity.Mapper;

import com.webAnalytic.Entity.User;
import com.webAnalytic.Entity.WebSite;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class WebSiteMapper implements IMapper<WebSite> {

    @Override
    public WebSite map(ResultSet resultSet) throws SQLException {

        var user = new User(resultSet.getLong("Admin_id"));

        return new WebSite(
                resultSet.getLong("id"),
                resultSet.getString("domain"),
                user,
                resultSet.getDate("DateTime")
                );
    }
}