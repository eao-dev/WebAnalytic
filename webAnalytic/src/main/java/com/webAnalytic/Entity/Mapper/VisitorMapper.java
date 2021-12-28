package com.webAnalytic.Entity.Mapper;

import com.webAnalytic.Entity.Visitor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class VisitorMapper implements IMapper<Visitor> {

    @Override
    public Visitor map(ResultSet resultSet) throws SQLException {
        return new Visitor(resultSet.getLong("id"),
                resultSet.getString("Country"),
                resultSet.getString("Browser"),
                resultSet.getString("OS"),
                resultSet.getString("Device"),
                resultSet.getString("ScResolution"),
                resultSet.getDate("DateReg")
        );
    }
}
