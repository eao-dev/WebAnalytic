package com.webAnalytic.DAO;

import com.webAnalytic.Entity.Mapper.Report.ReportMapperWithoutSource;
import com.webAnalytic.Entity.Report;
import com.webAnalytic.Entity.Mapper.IMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportDAO implements DAO<Report> {

    private final JDBCLayer jdbcLayer;
    private final IMapper<Report> reportMapper;
    private final ReportMapperWithoutSource reportMapperWithoutSource;

    @Autowired
    public ReportDAO(JDBCLayer jdbcLayer, IMapper<Report> reportMapper,
                     ReportMapperWithoutSource reportMapperWithoutSource) {
        this.jdbcLayer = jdbcLayer;
        this.reportMapper = reportMapper;
        this.reportMapperWithoutSource = reportMapperWithoutSource;
    }

    @Override
    public Report getById(long id) {
        assert (id > 0);
        String sqlQuery = "select * from [Report] where id=?";
        return jdbcLayer.select(sqlQuery, reportMapper, id).stream().findFirst().orElse(null);
    }

    @Override
    public Report getByObject(Report report) {
        assert (report != null);
        return getById(report.getId());
    }

    @Override
    public List<Report> listByObject(Object userId) {
        String sqlQuery = "select * from [Report] where User_id=?";
        return jdbcLayer.select(sqlQuery, reportMapperWithoutSource, userId);
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
