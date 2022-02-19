package com.webAnalytic.Controllers;

import com.webAnalytic.Services.AnalyticsService;
import com.webAnalytic.Services.UserService;
import com.webAnalytic.Services.WebSiteService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 *  This controller manages the output of analytical data.
 */

@Controller
@RequestMapping("analytics")
public class AnalyticsController extends BaseController {

    private final AnalyticsService statisticService;
    private final WebSiteService webSiteService;
    private final UserService userService;

    @Autowired
    public AnalyticsController(AnalyticsService statisticService, WebSiteService webSiteService, UserService userService) {
        this.statisticService = statisticService;
        this.webSiteService = webSiteService;
        this.userService = userService;
    }

    @GetMapping("{id}")
    public String analytics(Model model, @PathVariable("id") Long siteId) throws Exception {
        var userAuth = authCurrentUser();

        if (!userService.hasAccess(siteId, userAuth.getId()))
            throw new RuntimeException("Error: 403, Forbidden!");

        model.addAttribute("userAuth", userAuth);
        model.addAttribute("funcInfo", statisticService.getFunctionInfo().toString());
        model.addAttribute("siteId", siteId);
        model.addAttribute("currentDate", new Date());
        model.addAttribute("domain", webSiteService.getById(siteId).getDomain());

        List<String> dateRange = statisticService.getDateRange(siteId);
        model.addAttribute("dateMin", dateRange.get(0));
        model.addAttribute("dateMax", dateRange.get(1));

        return "/analytics";
    }

    /**
     * Returns JSON-object contains full statistic about site;
     *
     * @param filterJson - JSON object contains parameters which need to be returned;
     * @return {@link JSONObject} analytic info.
     */
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> analytics(@RequestBody String filterJson) throws Exception {
        var out = statisticService.getStatistic(authCurrentUser(), new JSONObject(filterJson));
        return new ResponseEntity<>(out.toString(), HttpStatus.OK);
    }


}
