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
    public String main(Model model,
                       @ModelAttribute("userAuth") User userAuth,
                       @ModelAttribute("newUser") User newUser) throws Exception {
        model.addAttribute("userList", userService.getUsersList(userAuth));
        return "/userManagement";
    }

    @PostMapping("add")
    public String add(RedirectAttributes redirectAttributes,
                      @ModelAttribute("newUser") @Valid User newUser,
                      BindingResult br) throws Exception {

        if (br.hasErrors()) {
            return "/userManagement"; // todo:fix it
        }

        newUser.setUserAdminId(authCurrentUser().getId());

        if (!userService.create(newUser, UserRole.USER))
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка добавления! Возможно такой пользователь уже существует!");
        else
            redirectAttributes.addFlashAttribute("success", "Пользователь добавлен!");

        return "redirect:/userManagement";
    }

    @PatchMapping("edit")
    public String edit(RedirectAttributes redirectAttributes,
                       @ModelAttribute("userAuth") User userAuth,
                       @RequestParam("name") String name,
                       @RequestParam("password") String password,
                       @RequestParam("userId") long userId
    ) throws Exception {

        if (userService.updateUser(userAuth.getId(), userId, name, password))
            redirectAttributes.addFlashAttribute("success", "Данные успешно обновлены!");
        else
            redirectAttributes.addFlashAttribute("error", "Ошибка обновления!");

        return "redirect:/userManagement";
    }

    @DeleteMapping("delete")
    public String delete(RedirectAttributes redirectAttributes,
                         @ModelAttribute("userAuth") User userAuth,
                         @RequestParam(name = "userId") long userId)
            throws Exception {

        assert (userId > 0);

        if (!userService.deleteByID(userAuth.getId(), userId))
            redirectAttributes.addFlashAttribute("error", "Ошибка удаления пользователя!");
        else
            redirectAttributes.addFlashAttribute("success", "Пользователь успешно удалён!");

        return "redirect:/userManagement";
    }

    /**
     * Assignment of rights to a user to access the site; returns JSON-object contain status for current action;
     *
     * @param userAuth - current logged-in user;
     * @param userId   - id of user;
     * @param siteId   - id of website;
     */
    @PatchMapping("changePermissionSiteAccess")
    @ResponseBody
    public ResponseEntity<String> changePermissionSiteAccess(@ModelAttribute("userAuth") User userAuth,
                                                             @RequestParam(name = "userId") long userId,
                                                             @RequestParam(name = "siteId") long siteId,
                                                             @RequestParam(name = "state") boolean state
    ) throws Exception {

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
     * @param userAuth - current logged-in user;
     * @param siteId   - id of website;
     */
    @GetMapping("getPermission")
    @ResponseBody
    public ResponseEntity<String> getPermissionUserSite(@ModelAttribute("userAuth") User userAuth,
                                                        @RequestParam(name = "siteId") long siteId) throws Exception {
        var out = accessWebSiteService.usersListWithPermissions(userAuth, siteId);
        return outObject(out);
    }

}