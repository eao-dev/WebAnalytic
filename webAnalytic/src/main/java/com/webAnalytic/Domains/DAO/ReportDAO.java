package com.webAnalytic.Domains.DAO;

import com.webAnalytic.Auxiliary.JDBCLayer;
import com.webAnalytic.Domains.Report;
import com.webAnalytic.Domains.IMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportDAO implements DAO<Report> {

    private final JDBCLayer jdbcLayer;
    private final IMapper<Report> reportMapper;

    @Autowired
    public ReportDAO(JDBCLayer jdbcLayer, IMapper<Report> reportMapper) {
        this.jdbcLayer = jdbcLayer;
        this.reportMapper = reportMapper;
    }

    public Report getByIdWithSource(long id) {
        assert (id > 0);
        String sqlQuery = "select * from [Report] where id=?";
        return jdbcLayer.select(sqlQuery, reportMapper, id).stream().findFirst().orElse(null);
    }

    public Report getByIdWithoutSource(long id) {
        assert (id > 0);
        String sqlQuery = "select User_id, id, FileName from [Report] where id=?";
        return jdbcLayer.select(sqlQuery, reportMapper, id).stream().findFirst().orElse(null);
    }

    public List<Report> listByObjectWithoutSource(Object userId) {
        assert (userId != null);
        String sqlQuery = "select User_id, id, FileName from [Report] where User_id=?";
        return jdbcLayer.select(sqlQuery, reportMapper, userId);
    }

    @Override
    public boolean create(Report report) {
        assert (report != null);
        String sqlQuery = "insert into [Report] (User_id, FileName, Source) values (?,?,?)";
        return jdbcLayer.update(sqlQuery, report.getUser().getId(), report.getFileName(), report.getSource()) == 1;
    }

    @Override
    public boolean deleteById(long id) {
        assert (id > 0);
        String sqlQuery = "delete from [Report] where id=?";
        return jdbcLayer.update(sqlQuery, id) == 1;
    }
}
