package com.webAnalytic.DAO;

import com.webAnalytic.Entity.Mapper.IMapper;
import com.webAnalytic.Entity.User;
import com.webAnalytic.Entity.Visitor;
import com.webAnalytic.Entity.WebSite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WebSiteDAO implements DAO<WebSite> {

    private final JDBCLayer jdbcLayer;
    private final IMapper<WebSite> webSiteMapper;

    @Autowired
    public WebSiteDAO(JDBCLayer jdbcLayer, IMapper<WebSite> webSiteMapper) {
        this.jdbcLayer = jdbcLayer;
        this.webSiteMapper = webSiteMapper;
    }

    @Override
    public WebSite getById(long id) {
        assert (id > 0);
        String sqlQuery = "select * from [WebSite] where id = ?";
        return jdbcLayer.select(sqlQuery, webSiteMapper, id).stream().findFirst().orElse(null);
    }

    @Override
    public WebSite getByObject(WebSite object) {
        assert (object != null);
        String sqlQuery = "select * from [WebSite] where [WebSite].domain=?";
        return jdbcLayer.select(sqlQuery, webSiteMapper, object.getId(), object.getDomain()).
                stream().findFirst().orElse(null);
    }

    @Override
    public List<WebSite> listByObject(Object user) {
        assert (user != null);
        String sqlQuery = "select * from [WebSite] where Admin_id = ?";
        return jdbcLayer.select(sqlQuery, webSiteMapper, ((User) user).getId());
    }

    @Override
    public List<WebSite> list(){
        String sqlQuery = "select * from [WebSite]";
        return jdbcLayer.select(sqlQuery, webSiteMapper);
    }

    @Override
    public boolean create(WebSite webSite) {
        assert (webSite != null);
        String sqlQuery = "insert into [WebSite] (Domain, Admin_id) values (?, ?)";
        return jdbcLayer.update(sqlQuery, webSite.getDomain(), webSite.getAdmin().getId()) == 1;
    }

    public boolean createWithLastInsertedId(WebSite webSite) {
        assert (webSite != null);
        String sqlQuery = "insert into [WebSite] (Domain, Admin_id) values (?, ?)";
        ArrayList<Long> arrayListInserted = new ArrayList<>();

        if (jdbcLayer.update(sqlQuery, arrayListInserted, "id",
                webSite.getDomain(), webSite.getAdmin().getId()) == 0)
            return false;

        webSite.setId(arrayListInserted.stream().findFirst().orElse(0L));
        return (webSite.getId()>0);
    }

    @Override
    public boolean deleteByObject(WebSite webSite) {
        assert (webSite != null);
        return deleteById(webSite.getId());
    }

    @Override
    public boolean deleteById(long id) {
        assert (id > 0);
        String sqlQuery = "delete from [WebSite] where id=?";
        return jdbcLayer.update(sqlQuery, id) == 1;
    }

}
