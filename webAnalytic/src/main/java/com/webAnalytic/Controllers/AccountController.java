package com.webAnalytic.Controllers;

import com.webAnalytic.Config.Security.Entity.UserRole;
import com.webAnalytic.Entity.User;
import com.webAnalytic.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * This controller manages the viewing, modification and deletion of an account.
 */

@Controller
@RequestMapping("/account")
public class AccountController extends BaseController {

    private final UserService userService;

    @Autowired
    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String account() {
        return "/account";
    }

    @GetMapping("registration")
    public String registration(@ModelAttribute("newUser") User newUser) {
        return "/registration";
    }

    @PostMapping("registration")
    public String registration(RedirectAttributes redirectAttributes, @ModelAttribute("newUser") @Valid User newUser,
                               BindingResult br)
            throws Exception {

        if (br.hasErrors()) {
            return "redirect:/registration"; // TODO:fix it
        }

        if (!userService.create(newUser, UserRole.ADMIN)) {
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка регистрации! Возможно такой пользователь уже существует!");
            return "redirect:/registration";
        }

        return "redirect:/login";
    }


    @PatchMapping("edit")
    public String edit(RedirectAttributes redirectAttributes, @ModelAttribute("userAuth") User userAuth,
                       @RequestParam("name") String name, @RequestParam("password") String password
    ) throws Exception {

        if (userService.updateMySelf(userAuth.getId(), name, password))
            redirectAttributes.addFlashAttribute("success", "Данные успешно обновлены!");
        else
            redirectAttributes.addFlashAttribute("error", "Ошибка обновления!");

        return "redirect:/account";
    }

    @DeleteMapping("delete")
    public String delete(RedirectAttributes redirectAttributes, @ModelAttribute("userAuth") User userAuth)
            throws Exception {

        if (userService.deleteMySelf(userAuth.getId())) {
            SecurityContextHolder.clearContext();
            return "redirect:/login";
        } else
            redirectAttributes.addFlashAttribute("error", "Не удалось удалить учётную запись!");

        return "redirect:/account";
    }
}
