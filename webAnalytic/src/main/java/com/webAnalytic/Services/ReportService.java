package com.webAnalytic.Services;

import com.webAnalytic.DAO.ReportDAO;
import com.webAnalytic.Entity.Report;
import com.webAnalytic.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    private final ReportDAO reportDAO;

    @Autowired
    public ReportService(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }


    private boolean checkOwnerReport(long userOwnerId, long reportId){
        Report report = reportDAO.getById(reportId);
        return (report.getUser().getId()==userOwnerId);
    }

    public ByteArrayResource getSourceReport(long reportOwnerId, long reportId) {

        if (!checkOwnerReport(reportOwnerId,reportId))
            return null;

        return new ByteArrayResource(reportDAO.getById(reportId).getSource());
    }

    public List<Report> getListForUser(long userId) throws Exception {
        return reportDAO.listByObject(userId);
    }

    public boolean removeReport(long reportOwnerId, long reportId) {

        if (!checkOwnerReport(reportOwnerId,reportId))
            return false;

        return reportDAO.deleteById(reportId);
    }

    public boolean addReport(Long userId, String fileName, byte[] reportSource) throws Exception {

        Report report = new Report();
        report.setUser(new User(userId));
        report.setFileName(fileName);
        report.setSource(reportSource);

        return reportDAO.create(report);
    }

}
