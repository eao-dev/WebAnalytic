package com.webAnalytic.Domains.DAO;

import com.webAnalytic.Auxiliary.JDBCLayer;
import com.webAnalytic.Domains.IMapper;
import com.webAnalytic.Domains.Resource;
import com.webAnalytic.Domains.WebSite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ResourceDAO implements DAO<Resource> {

    private final JDBCLayer jdbcLayer;
    private final IMapper<Resource> resourceMapper;

    @Autowired
    public ResourceDAO(JDBCLayer jdbcLayer, IMapper<Resource> resourceMapper) {
        this.jdbcLayer = jdbcLayer;
        this.resourceMapper = resourceMapper;
    }

    @Override
    public Resource getById(long id) {
        assert (id > 0);
        String sqlQuery = "select * from [Resource] where id=?";
        return jdbcLayer.select(sqlQuery, resourceMapper, id).stream().findFirst().orElse(null);
    }

    @Override
    public Resource getByObject(Resource resource) {
        assert (resource != null);
        String sqlQuery = "select * from [Resource] where page = ?";
        return jdbcLayer.select(sqlQuery, resourceMapper, resource.getPage()).stream().findFirst().orElse(null);
    }

    @Override
    public List<Resource> listByObject(Object webSite) {
        assert (webSite != null);
        String sqlQuery = "select * from [Resource] inner join [WebSite] where webSite_id=?";
        return jdbcLayer.select(sqlQuery, resourceMapper, ((WebSite) (webSite)).getId());
    }

    @Override
    public boolean create(Resource resource) {
        assert (resource != null);
        String sqlQuery = "insert into [Resource] (WebSite_id, Page) values (?,?)";

        ArrayList<Long> arrayListInserted = new ArrayList<>();
        if (jdbcLayer.update(sqlQuery, arrayListInserted, "id",
                resource.getDomain().getId(), resource.getPage()) == 0)
            return false;

        resource.setId(arrayListInserted.stream().findFirst().orElse(0L));
        return (resource.getId() > 0);
    }

    @Override
    public boolean deleteById(long id) {
        assert (id > 0);
        String sqlQuery = "delete from [Resource] where id=?";
        return jdbcLayer.update(sqlQuery, id) == 1;
    }
}