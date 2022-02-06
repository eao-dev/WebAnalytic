package com.webAnalytic.DAO;

import com.webAnalytic.Entity.Mapper.IMapper;
import com.webAnalytic.Entity.Visit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;

@Component
public class VisitDAO implements DAO<Visit> {

    private final JDBCLayer jdbcLayer;
    private final IMapper<Visit> visitMapper;

    @Autowired
    public VisitDAO(JDBCLayer jdbcLayer, IMapper<Visit> visitMapper) {
        this.jdbcLayer = jdbcLayer;
        this.visitMapper = visitMapper;
    }

    @Override
    public Visit getById(long id) {
        assert (id > 0);
        String sqlQuery = "select Visit.id, referer.host as referer from [Visit] " +
                "join referer on referer.id = referer_id " +
                "where Visit.id = ?";
        return jdbcLayer.select(sqlQuery, visitMapper, id).stream().findFirst().orElse(null);
    }

    @Override
    public Visit getByObject(Visit visit) {
        assert (visit != null);
        return getById(visit.getID());
    }

    @Override
    public boolean create(Visit visit) {
        assert (visit != null);
        String sqlQuery = "exec CreateVisit ?,?,?";

        return jdbcLayer.update(sqlQuery,
                visit.getVisitor().getId(),
                visit.getTargetResource().getId(),
                visit.getReferer()) != 0;
    }

    @Override
    public boolean deleteByObject(Visit visit) {
        assert (visit != null);
        return deleteById(visit.getID());
    }

    @Override
    public boolean deleteById(long id) {
        assert (id > 0);
        String sqlQuery = "delete from [Visit] where id=?";
        return jdbcLayer.update(sqlQuery, id) == 1;
    }
}