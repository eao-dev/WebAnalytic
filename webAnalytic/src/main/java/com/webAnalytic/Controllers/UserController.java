package com.webAnalytic.Controllers;

import com.webAnalytic.Config.Security.Entity.UserRole;
import com.webAnalytic.Entity.User;
import com.webAnalytic.Services.AccessWebSiteService;
import com.webAnalytic.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/userManagement")
public class UserController extends BaseController {

    private final UserService userService;

    private final AccessWebSiteService accessWebSiteService;

    @Autowired
    public UserController(UserService userService, AccessWebSiteService accessWebSiteService) {
        this.userService = userService;
        this.accessWebSiteService = accessWebSiteService;
    }

    @GetMapping()
    public String main(Model model) throws Exception {
        var userAuth = authCurrentUser();
        model.addAttribute("userList", userService.getUsersList(userAuth));
        model.addAttribute("newUser",  new User());
        model.addAttribute("userAuth",  userAuth);
        return "/userManagement";
    }

    @PostMapping("add")
    public String add(RedirectAttributes redirectAttributes,
                      @ModelAttribute @Valid User newUser,
                      BindingResult br) throws Exception {

        if (br.hasErrors()) {
            return "/userManagement";
        }

        var userAuth = authCurrentUser();
        newUser.setUserAdminId(userAuth.getId());

        if (!userService.create(newUser, UserRole.USER))
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка добавления! Возможно такой пользователь уже существует!");
        else
            redirectAttributes.addFlashAttribute("success", "Пользователь добавлен!");

        return "redirect:/userManagement";
    }

    @PutMapping("edit")
    public String edit(RedirectAttributes redirectAttributes,
                       @RequestParam("name") String name,
                       @RequestParam("password") String password,
                       @RequestParam("userId") long userId
    ) throws Exception {

        var userAuth = authCurrentUser();
        if (userService.updateUser(userAuth.getId(), userId, name, password))
            redirectAttributes.addFlashAttribute("success", "Данные успешно обновлены!");
        else
            redirectAttributes.addFlashAttribute("error", "Ошибка обновления!");

        return "redirect:/userManagement";
    }

    @DeleteMapping("delete")
    public String delete(RedirectAttributes redirectAttributes,
                         @RequestParam(name = "userId") long userId)
            throws Exception {

        assert (userId > 0);

        var userAuth = authCurrentUser();
        if (!userService.deleteByID(userAuth.getId(), userId))
            redirectAttributes.addFlashAttribute("error", "Ошибка удаления пользователя!");
        else
            redirectAttributes.addFlashAttribute("success", "Пользователь успешно удалён!");

        return "redirect:/userManagement";
    }

    /**
     * Assignment of rights to a user to access the site; returns JSON-object contain status for current action;
     *
     * @param userId   - id of user;
     * @param siteId   - id of website;
     */
    @PutMapping("changePermissionSiteAccess")
    @ResponseBody
    public ResponseEntity<String> changePermissionSiteAccess(@RequestParam(name = "userId") long userId,
                                                             @RequestParam(name = "siteId") long siteId,
                                                             @RequestParam(name = "state") boolean state
    ) throws Exception {

        var userAuth = authCurrentUser();
        boolean status;
        if (state)
            status = accessWebSiteService.setAccess(userAuth.getId(), userId, siteId);
        else
            status = accessWebSiteService.deleteAccess(userAuth.getId(), userId, siteId);

        return outResult(status, "Успешно", "Ошибка изменения прав!", HttpStatus.ACCEPTED);
    }

    /**
     * Returns JSON-object contain users with permissions to the site;
     *
     * @param siteId   - id of website;
     */
    @GetMapping("getPermission")
    @ResponseBody
    public ResponseEntity<String> getPermissionUserSite(@RequestParam(name = "siteId") long siteId) throws Exception {

        var userAuth = authCurrentUser();
        var out = accessWebSiteService.usersListWithPermissions(userAuth, siteId);
        return outObject(out);
    }

}