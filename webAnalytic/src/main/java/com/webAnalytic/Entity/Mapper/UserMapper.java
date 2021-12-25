package com.webAnalytic.Entity.Mapper;

import com.webAnalytic.Entity.User;
import org.springframework.stereotype.Component;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserMapper implements IMapper<User> {

    @Override
    public User map(ResultSet resultSet) throws SQLException {

        return new User(
                resultSet.getLong("id")  ,
                resultSet.getString("login"),
                resultSet.getBytes("password"),
                resultSet.getString("name"),
                resultSet.getBoolean("isAdmin"),
                resultSet.getLong("UserAdmin_id")
        );
    }
}