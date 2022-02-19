package com.webAnalytic.Controllers;

import com.webAnalytic.Domains.WebSite;
import com.webAnalytic.Services.WebSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * This controller manages the list of sites.
 */

@Controller
@RequestMapping("/")
public class WebSiteController extends BaseController {

    private final WebSiteService webSiteService;

    @Autowired
    public WebSiteController(WebSiteService webSiteService) {
        this.webSiteService = webSiteService;
    }

    @GetMapping()
    public String websiteList(Model model) {
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
            redirectBindingResults(redirectAttributes, new PairObjectBindingResult("newWebSite", newWebSite, br));
            return "redirect:/";
        }

        if (webSiteService.create(newWebSite, authCurrentUser()))
            redirectAttributes.addFlashAttribute("success", "Веб-сайт добавлен!");
        else
            redirectAttributes.addFlashAttribute("error", "Ошибка добавления веб-сайта!");

        return "redirect:/";
    }

    @DeleteMapping("delete/{id}")
    public String delete(RedirectAttributes redirectAttributes, @PathVariable("id") Long siteId) throws Exception {

        if (webSiteService.delete(authCurrentUser().getId(), siteId))
            redirectAttributes.addFlashAttribute("success", "Веб-сайт успешно удалён!");
        else
            redirectAttributes.addFlashAttribute("error", "Ошибка удаления веб-сайта!");

        return "redirect:/";
    }

}