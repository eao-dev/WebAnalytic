package com.webAnalytic.DAO;

import com.webAnalytic.Entity.User;
import com.webAnalytic.Entity.Mapper.IMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDAO implements DAO<User> {

    private final JDBCLayer jdbcLayer;
    private final IMapper<User> userMapper;

    @Autowired
    public UserDAO(JDBCLayer jdbcLayer, IMapper<User> userMapper) {
        this.jdbcLayer = jdbcLayer;
        this.userMapper = userMapper;
    }

    @Override
    public User getById(long id) {
        assert (id > 0);
        String sqlQuery = "select * from [user] where id=?";
        return jdbcLayer.select(sqlQuery, userMapper, id).stream().findFirst().orElse(null);
    }

    @Override
    public User getByObject(User user) {
        assert (user != null);
        String sqlQuery = "select * from [user] where id=? or login=?";
        return jdbcLayer.select(sqlQuery, userMapper, user.getId(), user.getLogin()).
                stream().findFirst().orElse(null);
    }

    @Override
    public List<User> listByObject(Object user){
        assert (user != null);
        String sqlQuery = "select * from [user] where UserAdmin_id=?";
        return jdbcLayer.select(sqlQuery, userMapper, ((User)user).getId());
    }

    @Override
    public boolean create(User user) {
        assert (user != null);

        Object adminId = user.getUserAdminId()>0?user.getUserAdminId():null;
        String sqlQuery = "insert into [user] (login, password, name, isAdmin, UserAdmin_id) values (?,?,?,?,?)";
        return jdbcLayer.update(sqlQuery,
                user.getLogin(),
                user.getPassword(),
                user.getName(),
                user.isAdmin(),
                adminId
                ) == 1;
    }

    @Override
    public boolean update(User user) {
        assert (user != null);
        String sqlQuery = "update [user] set name=?, password=? where id=?";

        return jdbcLayer.update(sqlQuery,
                user.getName(),
                user.getPassword(),
                user.getId()
        ) == 1;
    }

    @Override
    public boolean deleteByObject(User user) {
        assert (user != null);
        String sqlQuery = "delete from [user] where id=? or login=?";
        return jdbcLayer.update(sqlQuery, user.getId(), user.getLogin()) == 1;
    }

    @Override
    public boolean deleteById(long id) {
        assert (id > 0);
        String sqlQuery = "delete from [user] where id=?";
        return jdbcLayer.update(sqlQuery, id) == 1;
    }
}
