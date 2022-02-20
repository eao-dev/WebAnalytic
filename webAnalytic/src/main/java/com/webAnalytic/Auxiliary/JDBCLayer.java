package com.webAnalytic.Auxiliary;

import com.webAnalytic.Domains.IMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements my wrapper over JDBC.
 * Uses Hikari connection pool.
 */

@Component
@Scope("singleton")
public class JDBCLayer {

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

    private HikariDataSource ds;

    private JDBCLayer() {
    }

    public static JDBCLayer getInstance() {

        if (instance == null)
            instance = new JDBCLayer();

        return instance;
    }

    @PostConstruct
    public void connect() throws SQLException {
        if (ds != null)
            return;

        final String connectionString = String.format("%s://%s:%d;database=%s;encrypt=false;" +
                        "loginTimeout=%d;user=%s;password=%s;",
                typeDb,
                server,
                port,
                dbName,
                timeout,
                login,
                password);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(connectionString);
        config.setMaximumPoolSize(10000);
        this.ds = new HikariDataSource(config);
    }

    @PreDestroy
    public boolean disconnect() {
        if (ds == null)
            return true;

        ds.close();
        if (!ds.isClosed())
            return false;

        ds = null;
        return true;
    }

    public Connection getConnection() throws SQLException {
        assert (ds != null);
        return ds.getConnection();
    }

    public <T> List<T> select(String sqlQuery, IMapper<T> mapper, Object... args) {
        assert (mapper != null);

        ArrayList<T> resultList = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        Connection connection = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sqlQuery);

            if (preparedStatement == null)
                throw new Exception("prepareStatement return null");

            // Set parameters
            for (int i = 0; i < args.length; ++i)
                preparedStatement.setObject(i + 1, args[i]);

            resultSet = preparedStatement.executeQuery();

            resultList = new ArrayList<>(resultSet.getRow());

            // Handle result
            while (resultSet.next())
                resultList.add(mapper.map(resultSet));


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            if (preparedStatement != null)
                preparedStatement.close();

            if (resultSet != null)
                resultSet.close();

            if (connection != null)
                connection.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return resultList;
    }

    /**
     * Execute query (insert, delete, update) and returns number of rows changed.
     *
     * @param sqlQuery - string with SQL-query;
     * @param args     - arguments for query.
     */
    public long update(String sqlQuery, Object... args) {
        return update(sqlQuery, null, null, args);
    }

    /**
     * Execute query (insert, delete, update) and returns the number of rows changed,
     * as well as the identifiers of the created records.
     *
     * @param sqlQuery           - string with SQL-query;
     * @param insertedIdArray    - generated ids will be placed in this list;
     * @param nameColumnInserted - the name of the column containing the identifier;
     * @param args               - arguments for query.
     * @return - number of rows affected;
     */
    public long update(String sqlQuery, List<Long> insertedIdArray, String nameColumnInserted, Object... args) {
        long result = 0;
        PreparedStatement preparedStatement = null;

        try {
            Connection connection = getConnection();

            if (insertedIdArray != null && nameColumnInserted != null)
                preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            else
                preparedStatement = connection.prepareStatement(sqlQuery);

            if (preparedStatement == null)
                throw new Exception("prepareStatement return null");

            for (int i = 0; i < args.length; ++i)
                preparedStatement.setObject(i + 1, args[i]);

            result = preparedStatement.executeUpdate();
            if (result > 0 && insertedIdArray != null && nameColumnInserted != null) {
                var resultSetGenKeys = preparedStatement.getGeneratedKeys();

                while (resultSetGenKeys.next())
                    insertedIdArray.add(resultSetGenKeys.getLong(1));

                resultSetGenKeys.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {

            if (preparedStatement != null)
                preparedStatement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return result;
    }

}
