package com.webAnalytic.Entity.Mapper.Report;

import com.webAnalytic.Entity.Mapper.IMapper;
import com.webAnalytic.Entity.Report;
import com.webAnalytic.Entity.User;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReportMapperWithoutSource implements IMapper<Report> {

    @Override
    public Report map(ResultSet resultSet) throws SQLException {
        User user = new User(resultSet.getLong("User_id"));
        return new Report(resultSet.getLong("id"), user,resultSet.getString("FileName"), null);
    }
}