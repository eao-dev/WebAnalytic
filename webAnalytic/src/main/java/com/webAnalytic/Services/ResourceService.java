package com.webAnalytic.Services;

import com.webAnalytic.Domains.DAO.ResourceDAO;
import com.webAnalytic.Domains.Resource;
import com.webAnalytic.Domains.WebSite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResourceService {

    @Autowired
    private final ResourceDAO resourceDAO;

    private final UserService userService;

    public ResourceService(ResourceDAO resourceDAO, UserService userService) {
        this.resourceDAO = resourceDAO;
        this.userService = userService;
    }

    /**
     * Remove resource;
     *
     * @param resourceId - id of resource;
     */
    public boolean delete(long resourceId) throws Exception {
        return  resourceDAO.deleteById(resourceId);
    }

    /**
     * Create new resource;
     *
     * @param resource - new resource;
     */
    public boolean create(Resource resource) throws Exception {
        return resourceDAO.create(resource);
    }


}
