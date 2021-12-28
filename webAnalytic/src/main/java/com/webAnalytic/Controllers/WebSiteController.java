package com.webAnalytic.Controllers;

import com.webAnalytic.Entity.User;
import com.webAnalytic.Entity.WebSite;
import com.webAnalytic.Services.Analyze.AnalyzeService;
import com.webAnalytic.Services.ResourceService;
import com.webAnalytic.Services.UserService;
import com.webAnalytic.Services.WebSiteService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/")
public class WebSiteController extends BaseController {

    private final AnalyzeService statisticService;
    private final ResourceService resourceService;
    private final WebSiteService webSiteService;
    private final UserService userService;

    @Autowired
    public WebSiteController(WebSiteService webSiteService, UserService userService, ResourceService resourceService,
                             AnalyzeService statisticService) {
        this.webSiteService = webSiteService;
        this.userService = userService;
        this.resourceService = resourceService;
        this.statisticService = statisticService;
    }

    @GetMapping()
    public String websiteList(Model model) throws Exception {
        var userAuth = authCurrentUser();
        model.addAttribute("userAuth", userAuth);

        if (model.getAttribute("newWebSite") == null)
            model.addAttribute("newWebSite", new WebSite());

        model.addAttribute("webSiteList", webSiteService.getWebSiteList(userAuth));
        return "/ws";
    }

    @PostMapping("add")
    public String add(RedirectAttributes redirectAttributes,
                      @ModelAttribute("newWebSite") @Valid WebSite newWebSite, BindingResult br)
            throws Exception {

        if (br.hasErrors()) {
            redirectBindingResults(redirectAttributes, new PairObjectBr("newWebSite", newWebSite, br));
            return "redirect:/";
        }

        var userAuth = authCurrentUser();
        if (webSiteService.create(newWebSite, userAuth))
            redirectAttributes.addFlashAttribute("success", "Веб-сайт добавлен!");
        else
            redirectAttributes.addFlashAttribute("error", "Ошибка добавления веб-сайта!");

        return "redirect:/";
    }

    @DeleteMapping("delete")
    public String delete(RedirectAttributes redirectAttributes,
                         @RequestParam(name = "siteId") long siteId) throws Exception {

        var userAuth = authCurrentUser();
        if (webSiteService.delete(userAuth.getId(), siteId))
            redirectAttributes.addFlashAttribute("success", "Веб-сайт успешно удалён!");
        else
            redirectAttributes.addFlashAttribute("error", "Ошибка удаления веб-сайта!");

        return "redirect:/";
    }

    @GetMapping("analytics")
    public String analytics(Model model, @RequestParam(name = "siteId") long siteId) throws Exception {
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

    @DeleteMapping("clear")
    public String clear(RedirectAttributes redirectAttributes, @RequestParam(name = "siteId") long siteId)
            throws Exception {

        assert (siteId > 0);

        var userAuth = authCurrentUser();
        if (resourceService.deleteForSite(userAuth.getId(), siteId))
            redirectAttributes.addFlashAttribute("success", "Данные о посещениях очищены!");
        else
            redirectAttributes.addFlashAttribute("error", "Ошибка удаления данных!");

        return "redirect:/";
    }

    /**
     * Returns JSON-object contains full statistic about site;
     *
     * @param filterJson - JSON object contains parameters which need to be returned;
     * @return {@link JSONObject} analytic info.
     */
    @RequestMapping(value = "analytics", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> analytics(@RequestBody String filterJson) throws Exception {
        var userAuth = authCurrentUser();
        var outObject = statisticService.getStatistic(userAuth, new JSONObject(filterJson));
        return outObject(outObject);
    }

}