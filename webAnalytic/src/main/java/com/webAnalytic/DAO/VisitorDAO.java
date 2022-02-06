package com.webAnalytic.DAO;

import com.webAnalytic.Entity.Mapper.IMapper;
import com.webAnalytic.Entity.Visitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;

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
        String sqlQuery = "select Visitor.id as id, country.iso_code as country, browser.name as browser," +
                " device.name as device, ScResolution.value as ScResolution, os.name as os, DateReg from [Visitor] " +
                "join country on country.id=country_id " +
                "join device on device.id=device_id " +
                "join browser on browser.id=browser_id " +
                "join os on os.id=os_id " +
                "join ScResolution on ScResolution.id=ScResolution_id " +
                "where Visitor.id=?";
        return jdbcLayer.select(sqlQuery, visitorMapper, id).stream().findFirst().orElse(null);
    }

    @Override
    public Visitor getByObject(Visitor visitor) {
        assert (visitor != null);
        return getById(visitor.getId());
    }

    public boolean createWithLastInsertedId(Visitor visitor) {
        assert (visitor != null);
        String sqlQuery = "exec CreateVisitor ?,?,?,?,?";

        if (jdbcLayer.update(sqlQuery,  visitor.getCountry(),
                visitor.getBrowser(), visitor.getOS(),
                visitor.getScResolution(), visitor.getDevice()) == 0)
            return false;

        String sqlQueryIdentity = "select @@identity";
        IMapper<Long> mapper = (ResultSet resultSet) -> resultSet.getLong(1);

        Long id = jdbcLayer.select(sqlQueryIdentity, mapper).stream().findFirst().orElse(null);
        if (id == null)
            return false;

        visitor.setId(id);
        return true;
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