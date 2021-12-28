package com.webAnalytic.Controllers;

import com.webAnalytic.Entity.User;
import com.webAnalytic.Services.ReportService;
import com.webAnalytic.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("reports")
public class ReportController extends BaseController {

    private final ReportService reportService;
    private final UserService userService;

    @Autowired
    public ReportController(ReportService reportService, UserService userService) {
        this.reportService = reportService;
        this.userService = userService;
    }

    @GetMapping
    public String main(Model model) throws Exception {
        var userAuth = authCurrentUser();
        model.addAttribute("userAuth", userAuth);
        model.addAttribute("reportsList", reportService.getListForUser(userAuth.getId()));
        return "/reports";
    }

    @DeleteMapping("delete")
    public String delete(RedirectAttributes redirectAttributes,
                         @RequestParam(name = "reportId") long reportId) {

        var userAuth = authCurrentUser();
        if (reportService.removeReport(userAuth.getId(), reportId))
            redirectAttributes.addFlashAttribute("success", "Отчёт успешно удалён!");
        else
            redirectAttributes.addFlashAttribute("error", "Ошибка удаления отчёта!");

        return "redirect:/reports";
    }

    @GetMapping("download")
    public ResponseEntity<Resource> download(Model model, @RequestParam(name = "reportId") long reportId) {

        var userAuth = authCurrentUser();
        model.addAttribute("userAuth", userAuth);
        var out = reportService.getSourceReport(userAuth.getId(), reportId);

        return ResponseEntity.ok()
                .contentLength(out.contentLength())
                .contentType(MediaType.TEXT_HTML)
                .body(out);
    }

    /**
     * Added report;
     *
     * @param fileName     - report file name;
     * @param reportSource - HTML code of report;
     */
    @PostMapping("add")
    @ResponseBody
    public ResponseEntity add(@RequestParam(name = "fileName") String fileName,
                              @RequestParam(name = "reportSource") String reportSource) throws Exception {

        var userAuth = authCurrentUser();
        if (!reportService.addReport(userAuth.getId(), fileName, reportSource.getBytes()))
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
