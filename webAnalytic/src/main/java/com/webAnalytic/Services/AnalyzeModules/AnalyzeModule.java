package com.webAnalytic.Services.AnalyzeModules;

import com.webAnalytic.Entity.Mapper.IMapper;
import com.webAnalytic.DAO.JDBCLayer;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyzeModule {

    private final JDBCLayer jdbcLayer;

    private final long webSiteId;

    private final Date dateFrom;

    private final Date dateTo;

    public AnalyzeModule(JDBCLayer jdbcLayer, long webSiteId, Date dateFrom, Date dateTo) {
        this.jdbcLayer = jdbcLayer;
        this.webSiteId = webSiteId;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public long getWebSiteId() {
        return webSiteId;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    private long getLongParamFromScalarFunc(final String funcName) {
        assert(funcName!=null && !funcName.isEmpty());

        IMapper<Long> handler = (ResultSet resultSet) -> resultSet.getLong("");

        String sqlQuery = String.format("select %s(?,?,?)", funcName);

        var list = jdbcLayer.select(sqlQuery, handler, dateFrom, dateTo, webSiteId);
        if (list == null)
            return 0L;

        return list.stream().findFirst().orElse(0L);
    }

    private JSONObject getArrayIncludeObject_2columns(final String funcName, final String column1, final String column2) {
        assert(funcName!=null && !funcName.isEmpty());

        IMapper<Map<String, Long>> handler = (ResultSet resultSet) -> {
            Map<String, Long> ret = new HashMap<>();
            ret.put(resultSet.getString(column1), resultSet.getLong(column2));
            return ret;
        };

        String sqlQuery = String.format("select * from %s(?,?,?)", funcName);

        JSONObject retJsonObject = new JSONObject();
        var listObject = jdbcLayer.select(sqlQuery, handler, dateFrom, dateTo, webSiteId);

        if (listObject == null)
            return retJsonObject;

        for (var mapItem : listObject){
            for (var pair : mapItem.entrySet()){
                // todo: костыль
                String key = pair.getKey();
                if (key==null)
                    key="";

                retJsonObject.put(key, pair.getValue());
            }
        }

        return retJsonObject;
    }

    private JSONObject getArrayIncludeObject_3columns(final String funcName, final String column1,
                                                     final String column2, final String column3) {

        Map<String, List<JSONObject>> resultMap = new HashMap<>();

        IMapper mapper = (ResultSet resultSet) -> {
            var keyParam = resultSet.getString(column1);
            resultMap.putIfAbsent(keyParam, new ArrayList<>());

            JSONObject includedObject = new JSONObject();
            includedObject.put(String.valueOf(resultSet.getString(column2)), resultSet.getLong(column3));

            var cortage = resultMap.get(keyParam);
            cortage.add(includedObject);

            return null;
        };

        String sqlQuery = String.format("select * from %s(?,?,?)", funcName);
        jdbcLayer.select(sqlQuery, mapper, dateFrom, dateTo, webSiteId);

        JSONObject outObject = new JSONObject();
        for (var it: resultMap.entrySet()){

            JSONObject objectArray = new JSONObject();
            for (var object: it.getValue()){
                for (var key:object.keySet())
                    objectArray.put(key, object.get(key));
            }

            // todo: костыль
            String key = it.getKey();
            if (key==null)
                key="";

            outObject.put(key, objectArray);
        }

        return outObject;
    }

    // GENERAL

    public long allUniqueVisitorsJsonObj() {
        return getLongParamFromScalarFunc("[dbo].[allUniqueVisitor]");
    }

    public Long allCountVisitedResourceJsonObj() {
        return getLongParamFromScalarFunc("[dbo].[allCountVisitedRes]");
    }

    public Long avgCountVisitedResourceJsonObj() {
        return getLongParamFromScalarFunc("[dbo].[avgCountVisitedRes]");
    }

    public Long newVisitorCountJsonObj() {
        return getLongParamFromScalarFunc("[dbo].[newVisitorCount]");
    }

    public JSONObject statVisitedOnDayJsonArr() {
        return getArrayIncludeObject_2columns("statVisitOnDay", "Day", "cnt");
    }

    public JSONObject statResourceJsonArr() {
        return getArrayIncludeObject_2columns("statResource","Page", "cnt");
    }

    public JSONObject statRefererJsonArr() {
        return getArrayIncludeObject_2columns("statReferer","Referer", "cnt");
    }

    // Audience

    public JSONObject audienceStatBrowserJsonArr() {
        return getArrayIncludeObject_2columns("statBrowser", "Browser", "cnt");
    }

    public JSONObject audienceStatOSJsonArr() {
        return getArrayIncludeObject_2columns("statOS", "OS","cnt");
    }

    public JSONObject audienceStatDeviceJsonArr() {
        return getArrayIncludeObject_2columns("statDevice", "Device", "cnt");
    }

    public JSONObject audienceStatScResolutionJsonArr() {
        return getArrayIncludeObject_2columns("statScResolution", "ScResolution", "cnt");
    }

    public JSONObject audienceStatCountryJsonArr() {
        return getArrayIncludeObject_2columns("statCountry", "Country", "cnt");
    }

    // Audience resource

    public JSONObject audienceResStatBrowserJsonObj() {
        return getArrayIncludeObject_3columns("statResBrowser", "page", "Browser", "cnt");
    }

    public JSONObject audienceResStatOSJsonObj() {
        return getArrayIncludeObject_3columns("statResOS", "page","OS", "cnt");
    }

    public JSONObject audienceResStatDeviceJsonObj() {
        return getArrayIncludeObject_3columns("statResDevice", "page","Device", "cnt");
    }

    public JSONObject audienceResStatScResolutionJsonObj() {
        return getArrayIncludeObject_3columns("statResScResolution", "page","ScResolution", "cnt");
    }

    public JSONObject audienceResStatCountryJsonObj() {
        return getArrayIncludeObject_3columns("statResCountry", "page","Country", "cnt");
    }

    // Audience referer

    public JSONObject audienceRefStatBrowserJsonObj() {
        return getArrayIncludeObject_3columns("statRefBrowser", "Referer", "Browser", "cnt");
    }

    public JSONObject audienceRefStatOSJsonObj() {
        return getArrayIncludeObject_3columns("statRefOS", "Referer","OS", "cnt");
    }

    public JSONObject audienceRefStatDeviceJsonObj() {
        return getArrayIncludeObject_3columns("statRefDevice", "Referer","Device", "cnt");
    }

    public JSONObject audienceRefStatScResolutionJsonObj() {
        return getArrayIncludeObject_3columns("statRefScResolution", "Referer","ScResolution", "cnt");
    }

    public JSONObject audienceRefStatCountryJsonObj() {
        return getArrayIncludeObject_3columns("statRefCountry", "Referer","Country", "cnt");
    }

    public List<String> dateRange() {
        IMapper<List<String>> handler = (ResultSet resultSet) -> {

            ArrayList<String> outResult = new ArrayList<>();
            var minDate = resultSet.getDate("min");
            var maxDate = resultSet.getDate("max");
            String outMinDate, outMaxDate;
            if (minDate==null) outMinDate = ""; else outMinDate = minDate.toString();
            if (maxDate==null) outMaxDate = ""; else outMaxDate = maxDate.toString();

            outResult.add(outMinDate);
            outResult.add(outMaxDate);
            return outResult;
        };

        return jdbcLayer.select("select * from dateRange(?)", handler, webSiteId).stream().
                findFirst().orElse(null);
    }

}
