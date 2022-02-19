package com.webAnalytic.Services;

import com.webAnalytic.Auxiliary.JDBCLayer;
import com.webAnalytic.Domains.IMapper;
import com.webAnalytic.Domains.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AnalyticsService {

    private enum GROUPS {
        generalGroup(0), audienceGroup(1), audienceResGroup(2), audienceRefGroup(3);

        private Integer value;

        GROUPS(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }

    private static final int groupNameIdx = 0;
    private static final int describeIdx = 1;
    private static final int funcIdx = 2;
    private static final int paramCntIdx = 3;

    private static final Object[][] arrAnalyticsFunc = {
            {GROUPS.generalGroup, "Количество уникальных посетителей", "allUniqueVisitor", 0},
            {GROUPS.generalGroup, "Количество просмотренных страниц", "allCountVisitedRes", 0},
            {GROUPS.generalGroup, "Среднее количество просмотренных страниц", "avgCountVisitedRes", 0},
            {GROUPS.generalGroup, "Количество новых посетителей за день", "newVisitorCount", 0},
            {GROUPS.generalGroup, "Количество посетителей в день", "statVisitOnDay", 1},
            {GROUPS.generalGroup, "Статистика посещения каждой страницы", "statResource", 1},
            {GROUPS.generalGroup, "Статистика переходов с внешних ресурсов", "statReferer", 1},

            {GROUPS.audienceGroup, "Целевая аудитория: страна", "statBrowser", 1},
            {GROUPS.audienceGroup, "Целевая аудитория: браузеры", "statOS", 1},
            {GROUPS.audienceGroup, "Целевая аудитория: операционные системы", "statDevice", 1},
            {GROUPS.audienceGroup, "Целевая аудитория: устройства", "statScResolution", 1},
            {GROUPS.audienceGroup, "Целевая аудитория: разрешение экрана", "statCountry", 1},

            {GROUPS.audienceResGroup, "Целевая аудитория{страницы}: страна", "statResCountry", 2},
            {GROUPS.audienceResGroup, "Целевая аудитория{страницы}: браузеры", "statResBrowser", 2},
            {GROUPS.audienceResGroup, "Целевая аудитория{страницы}: операционные системы", "statResOS", 2},
            {GROUPS.audienceResGroup, "Целевая аудитория{страницы}: устройства", "statResDevice", 2},
            {GROUPS.audienceResGroup, "Целевая аудитория{страницы}: разрешение экрана", "statResScResolution", 2},

            {GROUPS.audienceRefGroup, "Целевая аудитория{переходы}: страна", "statRefCountry", 2},
            {GROUPS.audienceRefGroup, "Целевая аудитория{переходы}: браузеры", "statRefBrowser", 2},
            {GROUPS.audienceRefGroup, "Целевая аудитория{переходы}: операционные системы", "statRefOS", 2},
            {GROUPS.audienceRefGroup, "Целевая аудитория{переходы}: устройства", "statRefDevice", 2},
            {GROUPS.audienceRefGroup, "Целевая аудитория{переходы}: разрешение экрана", "statRefScResolution", 2},
    };

    private final JDBCLayer jdbcLayer;

    private final OnlineStatService onlineStatService;

    private final UserService userService;

    @Value("${functionSchemaName}")
    private String schemaFuncName;

    @Autowired
    public AnalyticsService(JDBCLayer jdbcLayer, OnlineStatService onlineStatService, UserService userService) {
        this.jdbcLayer = jdbcLayer;
        this.onlineStatService = onlineStatService;
        this.userService = userService;
    }

    /**
     * Returns date range for visited of specified web-site;
     *
     * @param siteId - id of web-site;
     */
    public List<String> getDateRange(Long siteId) {
        IMapper<List<String>> mapper = (ResultSet resultSet) -> {

            ArrayList<String> outResult = new ArrayList<>();
            var minDate = resultSet.getDate("min");
            var maxDate = resultSet.getDate("max");
            String outMinDate, outMaxDate;

            if (minDate == null)
                outMinDate = "";
            else
                outMinDate = minDate.toString();

            if (maxDate == null)
                outMaxDate = "";
            else
                outMaxDate = maxDate.toString();

            outResult.add(outMinDate);
            outResult.add(outMaxDate);
            return outResult;
        };
        return jdbcLayer.select("select * from dateRange(?)", mapper, siteId).stream().
                findFirst().orElse(null);
    }

    /**
     * Returns JSON object contains info about analytic functions;
     */
    public JSONObject getFunctionInfo() {

        JSONObject ret = new JSONObject();
        JSONObject[] groups = new JSONObject[GROUPS.values().length];
        for (int i=0;i<groups.length;++i)
            groups[i]=new JSONObject();


        // Fill info about functions
        for (Object[] it : arrAnalyticsFunc) {
            GROUPS group = (GROUPS) it[groupNameIdx];
            int groupIdx = group.getValue();

            String describe = (String) it[describeIdx];
            String func = (String) it[funcIdx];
            groups[groupIdx].put(func, describe);
        }

        // Fill groups
        for (var group : GROUPS.values())
            ret.put(group.toString(), groups[group.getValue()]);

        return ret;
    }

    /**
     * Returns JSON object contains statistic about site;
     *
     * @param user   - user which has access to the data-analytics;
     * @param filter - JSON object contains parameters which need to be returned;
     * @return {@link JSONObject}.
     */
    public JSONObject getStatistic(User user, JSONObject filter) throws Exception {

        long siteId = filter.getInt("siteId");

        if (!userService.hasAccess(siteId, user.getId()))
            return null;

        // Date including the upper border
        final String dateToFromFilterStr = filter.getString("dateTo");
        var dateTo = Date.valueOf(LocalDate.parse(dateToFromFilterStr).plusDays(1).toString());
        var dateFrom = Date.valueOf(filter.getString("dateFrom"));

        // General statistic object
        JSONObject[] groups = new JSONObject[GROUPS.values().length];
        for (int i=0;i<groups.length;++i)
            groups[i]=new JSONObject();

        // Filling an object with data
        for (var analyticsFunc : arrAnalyticsFunc) {
            String funcName = (String) analyticsFunc[funcIdx];
            GROUPS group = (GROUPS) analyticsFunc[groupNameIdx];
            Integer paramsCount = (Integer) analyticsFunc[paramCntIdx];
            if (!filter.has(funcName)) continue;

            SQLAnalyzer sqlAnalyzer = new SQLAnalyzer(funcName, siteId, dateFrom, dateTo);
            Object out;
            switch (paramsCount) {
                case 0:
                    out = sqlAnalyzer.getLong();
                    break;
                case 1:
                    out = sqlAnalyzer.getArrayWith2columns();
                    break;
                case 2:
                    out = sqlAnalyzer.getArrayWith3columns();
                    break;
                default:
                    throw new Exception("Invalid argument!");
            }
            groups[group.getValue()].put(funcName, out);
        }

        // Online
        groups[GROUPS.generalGroup.getValue()].put("online", onlineStatService.getOnlineVisitors(siteId));

        JSONObject statistic = new JSONObject();
        for (var group : GROUPS.values())
            statistic.put(group.toString(), groups[group.getValue()]);

        return statistic;
    }

    class SQLAnalyzer {
        private final Date dateFrom;
        private final Date dateTo;
        private final Long siteId;

        private final String sqlQueryTable;
        private final String sqlQueryScalar;

        private final static String countNameColumn = "cnt";

        public SQLAnalyzer(String funcName, Long siteId, Date dateFrom, Date dateTo) {
            assert (funcName != null && !funcName.isEmpty());
            assert (siteId != null && siteId!=0);
            assert (dateFrom != null && dateTo!=null);

            this.dateFrom = dateFrom;
            this.dateTo = dateTo;
            this.siteId = siteId;

            String sqlFuncName = String.format("[%s].[%s]", schemaFuncName, funcName);
            this.sqlQueryTable = String.format("select * from %s(?,?,?)", sqlFuncName);
            this.sqlQueryScalar = String.format("select %s(?,?,?)", sqlFuncName);
        }

        public long getLong() {
            IMapper<Long> handler = (ResultSet resultSet) -> {
                long ret = 0L;
                try {
                    ret = resultSet.getLong(1);
                } catch (Exception ex) {
                    return ret;
                }
                return ret;
            };

            var list = jdbcLayer.select(sqlQueryScalar, handler, dateFrom, dateTo, siteId);
            if (list == null)
                return 0L;

            return list.stream().findFirst().orElse(0L);
        }

        public JSONObject getArrayWith2columns() {
            IMapper<Map<String, Long>> handler = (ResultSet resultSet) -> {
                Map<String, Long> ret = new HashMap<>();

                String column1 = resultSet.getString(1);
                long c2 = resultSet.getLong(countNameColumn);

                if (column1 == null)
                    column1 = "";

                ret.put(column1, c2);
                return ret;
            };

            JSONObject ret = new JSONObject();
            var listObject = jdbcLayer.select(sqlQueryTable, handler,
                    dateFrom, dateTo, siteId);

            if (listObject == null)
                return ret;

            for (var mapItem : listObject) {
                for (var pair : mapItem.entrySet())
                    ret.put(pair.getKey(), pair.getValue());
            }

            return ret;
        }

        public JSONObject getArrayWith3columns() {
            Map<String, List<JSONObject>> resultMap = new HashMap<>();

            IMapper<Void> mapper = (ResultSet resultSet) -> {
                String column1 = resultSet.getString(1);
                String column2 = resultSet.getString(2);
                long column3 = resultSet.getLong(countNameColumn);

                if (column1 == null)
                    column1 = "";
                if (column2 == null)
                    column2 = "";

                var keyParam = column1;
                resultMap.putIfAbsent(keyParam, new ArrayList<>());

                JSONObject includedObject = new JSONObject();
                includedObject.put(column2, column3);

                var tuple = resultMap.get(keyParam);
                tuple.add(includedObject);

                return null;
            };

            jdbcLayer.select(sqlQueryTable, mapper, dateFrom, dateTo, siteId);

            JSONObject ret = new JSONObject();
            for (var it : resultMap.entrySet()) {

                JSONObject objectArray = new JSONObject();
                for (var object : it.getValue()) {
                    for (var key : object.keySet())
                        objectArray.put(key, object.get(key));
                }

                ret.put(it.getKey(), objectArray);
            }

            return ret;
        }
    }


}