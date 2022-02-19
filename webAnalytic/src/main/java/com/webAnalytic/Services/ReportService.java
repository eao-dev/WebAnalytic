package com.webAnalytic.Services;

import com.webAnalytic.Domains.DAO.ReportDAO;
import com.webAnalytic.Domains.Report;
import com.webAnalytic.Domains.User;
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
        Report report = reportDAO.getByIdWithoutSource(reportId);
        return (report.getUser().getId()==userOwnerId);
    }

    public ByteArrayResource getSourceReport(long reportOwnerId, long reportId) {

        if (!checkOwnerReport(reportOwnerId,reportId))
            return null;

        return new ByteArrayResource(reportDAO.getByIdWithSource(reportId).getSource());
    }

    public List<Report> getListForUser(long userId) {
        return reportDAO.listByObjectWithoutSource(userId);
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
