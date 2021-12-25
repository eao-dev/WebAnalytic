package com.webAnalytic.Entity.Mapper;

import com.webAnalytic.Entity.Resource;
import com.webAnalytic.Entity.WebSite;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ResourceMapper implements IMapper<Resource> {
    @Override
    public Resource map(ResultSet resultSet) throws SQLException {
        return new Resource(
                resultSet.getLong("id"),
                new WebSite(resultSet.getLong("webSite_Id")),
                resultSet.getString("Page"));
    }
}
