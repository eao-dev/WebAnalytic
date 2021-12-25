package com.webAnalytic.DAO;

import com.webAnalytic.Entity.Mapper.IMapper;
import com.webAnalytic.Entity.Visit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        String sqlQuery = "select * from [Visit] where id = ?";
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
        String sqlQuery = "insert into [Visit] (Resource_id, Visitor_id, Referer) values (?,?,?)";

        String visitReferer = visit.getReferer();
        String referer = null;
        if (!visitReferer.isEmpty())
            referer = visitReferer;

        return jdbcLayer.update(sqlQuery,
                visit.getTargetResource().getId(),
                visit.getVisitor().getId(),
                referer) == 1;
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