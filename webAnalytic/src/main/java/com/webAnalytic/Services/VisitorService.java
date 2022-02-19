package com.webAnalytic.Services;

import com.webAnalytic.Domains.DAO.DAO;
import com.webAnalytic.Domains.Visitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VisitorService {

    @Autowired
    private final DAO<Visitor> visitorDAO;

    public VisitorService(DAO<Visitor> visitorDAO) {
        this.visitorDAO = visitorDAO;
    }

    /**
     * Returns visitor {@link Visitor} by id;
     *
     * @param id - id of visitor;
     */
    public Visitor getById(long id) throws Exception {
        return visitorDAO.getById(id);
    }

    /**
     * Create new  visitor;
     *
     * @param visitor - created visitor;
     * @return true if new visitor success created, otherwise false;
     */
    public boolean create(Visitor visitor) throws Exception {
        return visitorDAO.create(visitor);
    }

}