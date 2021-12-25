package com.webAnalytic.Entity.Mapper;

import com.webAnalytic.Entity.Resource;
import com.webAnalytic.Entity.Visit;
import com.webAnalytic.Entity.Visitor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class VisitMapper implements IMapper<Visit> {

    @Override
    public Visit map(ResultSet resultSet) throws SQLException {
        var visitor = new Visitor(resultSet.getLong("Visitor_id"));
        var resource = new Resource(resultSet.getLong("Resource_id"));

        return new Visit(
                resultSet.getLong("id"),
                resultSet.getString("referer"),
                resource,
                visitor,
                resultSet.getDate("DateTime"));
    }
}
