package com.webAnalytic.Services;

import com.webAnalytic.DAO.ResourceDAO;
import com.webAnalytic.Entity.Resource;
import com.webAnalytic.Entity.WebSite;
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

    /**
     * Delete all resources for the specified site;
     *
     * @param siteId - if od web-site;
     */
    public boolean deleteForSite(long ownerSiteId, long siteId) throws Exception {

        if (!userService.isOwnerWebSite(ownerSiteId, siteId))
            return false;

        ArrayList<Resource> arrResourcesWebSite = (ArrayList<Resource>) resourceDAO.listByObject(new WebSite(siteId));

        List<Long> resourceList = new ArrayList<>();
        for (var res : arrResourcesWebSite)
            resourceList.add(res.getId());

        return resourceDAO.deleteByListId(resourceList);
    }
}
