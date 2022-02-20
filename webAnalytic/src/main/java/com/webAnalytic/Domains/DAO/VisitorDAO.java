package com.webAnalytic.Domains.DAO;

import com.webAnalytic.Auxiliary.JDBCLayer;
import com.webAnalytic.Domains.IMapper;
import com.webAnalytic.Domains.Visitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;

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

    /**
     * Adds a new visitor to the database and assigns an ID to it.
     * */
    public boolean createWithId(Visitor visitor) {
        assert (visitor != null);

        // Add to DB bypass the JDBC layer
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        try {
            connection = jdbcLayer.getConnection();
            preparedStatement = connection.prepareStatement("exec CreateVisitor ?,?,?,?,?");

            if (preparedStatement == null)
                throw new Exception("prepareStatement returns null");

            preparedStatement.setObject(1, visitor.getCountry());
            preparedStatement.setObject(2, visitor.getBrowser());
            preparedStatement.setObject(3, visitor.getOS());
            preparedStatement.setObject(4, visitor.getScResolution());
            preparedStatement.setObject(5, visitor.getDevice());

            int rowsAffected = preparedStatement.executeUpdate();
            preparedStatement.close();

            if (rowsAffected > 0) {
                preparedStatement = connection.prepareStatement("select @@identity");

                if (preparedStatement == null)
                    throw new Exception("prepareStatement returns null");

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet==null)
                    throw new Exception("resultSet is null");

                if (resultSet.next()) {
                    visitor.setId(resultSet.getLong(1));
                    resultSet.close();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            if (preparedStatement != null)
                preparedStatement.close();

            if (connection != null)
                connection.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return (visitor.getId() != 0);
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