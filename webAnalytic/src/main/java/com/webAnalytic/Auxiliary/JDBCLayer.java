package com.webAnalytic.Auxiliary;

import com.webAnalytic.Domains.IMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This is class implemented patter singleton
 */

@Component
@Scope("singleton")
public class JDBCLayer {

    private Connection connection;

    private static JDBCLayer instance;

    @Value("${db.Server}")
    private String server;

    @Value("${db.Port}")
    private int port;

    @Value("${db.Name}")
    private String dbName;

    @Value("${db.Login}")
    private String login;

    @Value("${db.Password}")
    private String password;

    @Value("${db.type}")
    private String typeDb;

    @Value("${db.timeout}")
    private int timeout;

    private JDBCLayer() {
    }

    public static JDBCLayer getInstance() {

        if (instance == null)
            instance = new JDBCLayer();

        return instance;
    }

    @PostConstruct
    public boolean connect() throws SQLException {

        System.out.println("connect!");

        if (connection != null)
            return true;

        String connectionString = String.format("%s://%s:%d;database=%s;" +
                        "encrypt=false;loginTimeout=%d;", // trustServerCertificate=false;
                typeDb,
                server,
                port,
                dbName,
                timeout);
        connection = DriverManager.getConnection(connectionString, login, password);

        return (connection != null);
    }

    //@PreDestroy
    public boolean disconnect() {
        System.out.println("disconnect!");
        if (connection == null)
            return true;

        try {
            connection.close();
            if (connection.isClosed()) {
                connection = null;
                return true;
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return false;
    }

    public Connection getConnection() {
        return connection;
    }

    public <T> List<T> select(String sqlQuery, IMapper<T> mapper, Object... args) {
        assert (connection != null);
        assert (mapper != null);

        ArrayList<T> resultList = null;

        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sqlQuery);

            if (preparedStatement != null) {

                // Set parameters
                for (int i = 0; i < args.length; ++i)
                    preparedStatement.setObject(i + 1, args[i]);

                resultSet = preparedStatement.executeQuery();

                resultList = new ArrayList<>(resultSet.getRow());

                // Handle result
                while (resultSet.next())
                    resultList.add(mapper.map(resultSet));

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
                if (resultSet != null)
                    resultSet.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return resultList;
    }

    public long update(String sqlQuery, Object... args) {
        return update(sqlQuery, null, null, args);
    }

    public long update(String sqlQuery, List<Long> insertedColumnArray, String nameColumnInserted, Object... args) {
        assert (connection != null);

        long result = 0;
        PreparedStatement preparedStatement = null;

        try {
            if (insertedColumnArray != null && nameColumnInserted != null)
                preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            else
                preparedStatement = connection.prepareStatement(sqlQuery);

            if (preparedStatement != null) {

                for (int i = 0; i < args.length; ++i)
                    preparedStatement.setObject(i + 1, args[i]);

                result = preparedStatement.executeUpdate();

                if (result > 0 && insertedColumnArray != null && nameColumnInserted != null) {
                    var resultSetGenKeys = preparedStatement.getGeneratedKeys();
                    while (resultSetGenKeys.next())
                        insertedColumnArray.add(resultSetGenKeys.getLong(1));
                    resultSetGenKeys.close();
                }

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {

            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return result;
    }

}
