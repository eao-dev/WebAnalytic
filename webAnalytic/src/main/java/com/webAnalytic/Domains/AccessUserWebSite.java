package com.webAnalytic.Domains;

import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AccessUserWebSite implements IMapper<AccessUserWebSite> {

    private WebSite webSite;
    private User user;

    public AccessUserWebSite() {}

    public AccessUserWebSite(WebSite webSite, User user)  {
        this.webSite = webSite;
        this.user = user;
    }

    public WebSite getWebSite() {
        return webSite;
    }

    public void setWebSite(WebSite webSite) {
        this.webSite = webSite;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public AccessUserWebSite map(ResultSet resultSet) throws SQLException {
        User user = new User(
                resultSet.getLong("User_id"),
                resultSet.getString("UserLogin"),
                resultSet.getBytes("UserPassword"),
                resultSet.getString("UserName"),
                false,
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
