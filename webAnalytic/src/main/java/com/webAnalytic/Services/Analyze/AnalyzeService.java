package com.webAnalytic.Services.Analyze;

import com.webAnalytic.DAO.JDBCLayer;
import com.webAnalytic.Entity.User;
import com.webAnalytic.Services.AnalyzeModules.AnalyzeModule;
import com.webAnalytic.Services.OnlineStatService;
import com.webAnalytic.Services.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class AnalyzeService {

    private final JDBCLayer jdbcLayer;

    private final OnlineStatService onlineStatService;

    private final UserService userService;

    @Autowired
    public AnalyzeService(JDBCLayer jdbcLayer, OnlineStatService onlineStatService, UserService userService) {
        this.jdbcLayer = jdbcLayer;
        this.onlineStatService = onlineStatService;
        this.userService = userService;
    }

    /**
     * Returns date range for visited of specified web-site;
     *
     * @param siteId - id of web-site;
     */
    public List<String> getDateRange(long siteId) {
        AnalyzeModule analyzeModule = new AnalyzeModule(jdbcLayer, siteId, null, null);
        return analyzeModule.dateRange();
    }

    /**
     * Returns JSON-object contains info about analytic-functions;
     */
    public JSONObject getFunctionInfo() {
        JSONObject funcInfo = new JSONObject();
        funcInfo.put("onlineVisitors", "Количество  посетителей в режимер реального времени");
        funcInfo.put("allUniqueVisitors", "Количество уникальных посетителей");
        funcInfo.put("allCountVisitedResource", "Количество просмотренных страниц");
        funcInfo.put("avgCountVisitedResource", "Среднее количество просмотренных страниц");
        funcInfo.put("newVisitorCount", "Количество новых посетителей за день");
        funcInfo.put("statVisitOnDay", "Количество посетителей в день");
        funcInfo.put("statResource", "Статистика посещения каждой страницы");
        funcInfo.put("statReferer", "Статистика переходов с внешних ресурсов");

        funcInfo.put("Audience_browser", "Целевая аудитория: страна");
        funcInfo.put("Audience_Country", "Целевая аудитория: браузеры");
        funcInfo.put("Audience_OS", "Целевая аудитория: операционные системы");
        funcInfo.put("Audience_Device", "Целевая аудитория: устройства");
        funcInfo.put("Audience_ScResolution", "Целевая аудитория: разрешение экрана");

        funcInfo.put("Audience_res_browser", "Целевая аудитория(страницы): страна");
        funcInfo.put("Audience_res_Country", "Целевая аудитория(страницы): браузеры");
        funcInfo.put("Audience_res_OS", "Целевая аудитория(страницы): операционные системы");
        funcInfo.put("Audience_res_Device", "Целевая аудитория(страницы): устройства");
        funcInfo.put("Audience_res_ScResolution", "Целевая аудитория(страницы): разрешение экрана");

        funcInfo.put("Audience_ref_browser", "Целевая аудитория(переходы): страна");
        funcInfo.put("Audience_ref_Country", "Целевая аудитория(переходы): браузеры");
        funcInfo.put("Audience_ref_OS", "Целевая аудитория(переходы): операционные системы");
        funcInfo.put("Audience_ref_Device", "Целевая аудитория(переходы): устройства");
        funcInfo.put("Audience_ref_ScResolution", "Целевая аудитория(переходы): разрешение экрана");
        return funcInfo;
    }

    /**
     * Returns JSON-object contains statistic about site using StatisticModule1;
     *
     * @param user   - user which has access to the data-analytics;
     * @param filter - JSON object contains parameters which need to be returned;
     * @return {@link JSONObject}
     */
    public JSONObject getStatistic(User user, JSONObject filter) throws Exception {

        long siteId = filter.getInt("siteId");

        if (!userService.hasAccess(siteId, user.getId()))
            return null;

        // Including the upper border
        final String dateToFromFilterStr = filter.getString("dateTo");
        var dateTo = Date.valueOf(LocalDate.parse(dateToFromFilterStr).plusDays(1).toString());

        var dateFrom = Date.valueOf(filter.getString("dateFrom"));

        AnalyzeModule analyzeModule = new AnalyzeModule(jdbcLayer, siteId, dateFrom, dateTo);

        // General statistic object
        JSONObject generalInfoJsonObj = new JSONObject();

        if (filter.has("onlineVisitors"))
            generalInfoJsonObj.put("onlineVisitors", onlineStatService.getOnlineVisitors(siteId));

        if (filter.has("allUniqueVisitors"))
            generalInfoJsonObj.put("allUniqueVisitors", analyzeModule.allUniqueVisitorsJsonObj());

        if (filter.has("allCountVisitedResource"))
            generalInfoJsonObj.put("allCountVisitedResource", analyzeModule.allCountVisitedResourceJsonObj());

        if (filter.has("avgCountVisitedResource"))
            generalInfoJsonObj.put("avgCountVisitedResource", analyzeModule.avgCountVisitedResourceJsonObj());

        if (filter.has("newVisitorCount"))
            generalInfoJsonObj.put("newVisitorCount", analyzeModule.newVisitorCountJsonObj());

        if (filter.has("statVisitOnDay"))
            generalInfoJsonObj.put("statVisitOnDay", analyzeModule.statVisitedOnDayJsonArr());

        if (filter.has("statResource"))
            generalInfoJsonObj.put("statResource", analyzeModule.statResourceJsonArr());

        if (filter.has("statReferer"))
            generalInfoJsonObj.put("statReferer", analyzeModule.statRefererJsonArr());

        // Audience statistic
        JSONObject audienceStat = new JSONObject();

        if (filter.has("Audience_browser"))
            audienceStat.put("browser", analyzeModule.audienceStatBrowserJsonArr());

        if (filter.has("Audience_OS"))
            audienceStat.put("OS", analyzeModule.audienceStatOSJsonArr());

        if (filter.has("Audience_Device"))
            audienceStat.put("Device", analyzeModule.audienceStatDeviceJsonArr());

        if (filter.has("Audience_ScResolution"))
            audienceStat.put("ScResolution", analyzeModule.audienceStatScResolutionJsonArr());

        if (filter.has("Audience_Country"))
            audienceStat.put("Country", analyzeModule.audienceStatCountryJsonArr());

        // Audience resources statistic
        JSONObject audienceResStat = new JSONObject();
        if (filter.has("Audience_res_browser"))
            audienceResStat.put("browser", analyzeModule.audienceResStatBrowserJsonObj());

        if (filter.has("Audience_res_OS"))
            audienceResStat.put("OS", analyzeModule.audienceResStatOSJsonObj());

        if (filter.has("Audience_res_Device"))
            audienceResStat.put("Device", analyzeModule.audienceResStatDeviceJsonObj());

        if (filter.has("Audience_res_ScResolution"))
            audienceResStat.put("ScResolution", analyzeModule.audienceResStatScResolutionJsonObj());

        if (filter.has("Audience_res_Country"))
            audienceResStat.put("Country", analyzeModule.audienceResStatCountryJsonObj());


        // Audience referer statistic
        JSONObject audienceRefStat = new JSONObject();

        if (filter.has("Audience_ref_browser"))
            audienceRefStat.put("browser", analyzeModule.audienceRefStatBrowserJsonObj());

        if (filter.has("Audience_ref_OS"))
            audienceRefStat.put("OS", analyzeModule.audienceRefStatOSJsonObj());

        if (filter.has("Audience_ref_Device"))
            audienceRefStat.put("Device", analyzeModule.audienceRefStatDeviceJsonObj());

        if (filter.has("Audience_ref_ScResolution"))
            audienceRefStat.put("ScResolution", analyzeModule.audienceRefStatScResolutionJsonObj());

        if (filter.has("Audience_ref_Country"))
            audienceRefStat.put("Country", analyzeModule.audienceRefStatCountryJsonObj());

        // Main output object
        JSONObject outObject = new JSONObject();
        outObject.put("general", generalInfoJsonObj);
        outObject.put("audience", audienceStat);
        outObject.put("audienceResource", audienceResStat);
        outObject.put("audienceReferer", audienceRefStat);

        return outObject;
    }

}