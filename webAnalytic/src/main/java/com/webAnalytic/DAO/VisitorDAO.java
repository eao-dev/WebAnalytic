package com.webAnalytic.DAO;

import com.webAnalytic.Entity.Mapper.IMapper;
import com.webAnalytic.Entity.Visitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class VisitorDAO implements DAO<Visitor> {

    private final JDBCLayer jdbcLayer;
    private final IMapper<Visitor> visitorMapper;

    @Autowired
    public VisitorDAO(JDBCLayer jdbcLayer, IMapper<Visitor> visitorMapper) {
        this.jdbcLayer = jdbcLayer;
        this.visitorMapper = visitorMapper;
    }

    @Override
    public Visitor getById(long id) {
        assert (id > 0);
        String sqlQuery = "select * from [Visitor] where id=?";
        return jdbcLayer.select(sqlQuery, visitorMapper, id).stream().findFirst().orElse(null);
    }

    @Override
    public Visitor getByObject(Visitor visitor) {
        assert (visitor != null);
        String sqlQuery = "select * from [Visitor] where id=?";
        return jdbcLayer.select(sqlQuery, visitorMapper, visitor.getId()).stream().findFirst().orElse(null);
    }

    public boolean createWithLastInsertedId(Visitor visitor) {
        assert (visitor != null);
        String sqlQuery = "insert into [Visitor] (Country, Browser, OS, Device, ScResolution) values (?,?,?,?,?)";
        ArrayList<Long> arrayListInserted = new ArrayList<>();

        if (jdbcLayer.update(sqlQuery, arrayListInserted, "id", visitor.getCountry(),
                visitor.getBrowser(), visitor.getOS(),
                visitor.getDevice(), visitor.getScResolution()) == 0)
            return false;

        visitor.setId(arrayListInserted.stream().findFirst().orElse(0L));
        return (visitor.getId()>0);
    }

    @Override
    public boolean deleteByObject(Visitor visitor) {
        assert (visitor != null);
        return deleteById(visitor.getId());
    }

    @Override
    public boolean deleteById(long id) {
        assert (id > 0);
        String sqlQuery = "delete from [Visitor] where id=?";
        return jdbcLayer.update(sqlQuery, id) == 1;
    }
}