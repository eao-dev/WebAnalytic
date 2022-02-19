package com.webAnalytic.Controllers;

import com.webAnalytic.Auxiliary.Config.Security.UserRole;
import com.webAnalytic.Domains.User;
import com.webAnalytic.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String account(Model model) {
        var userAuth = authCurrentUser();
        model.addAttribute("userAuth", userAuth);
        return "/account";
    }

    @GetMapping("registration")
    public String registration(Model model) {
        if (model.getAttribute("newUser") == null)
            model.addAttribute("newUser", new User());
        return "/registration";
    }

    @PostMapping("registration")
    public String registration(RedirectAttributes redirectAttributes, @ModelAttribute("newUser") @Valid User newUser,
                               BindingResult br) throws Exception {

        if (br.hasErrors()) {
            redirectBindingResults(redirectAttributes, new PairObjectBindingResult("newUser", newUser, br));
            return "redirect:registration";
        }

        if (!userService.create(newUser, UserRole.ADMIN)) {
            redirectAttributes.addFlashAttribute("error", "Ошибка регистрации!");
            return "redirect:registration";
        }

        return "redirect:/";
    }

    @PutMapping("edit")
    public String edit(RedirectAttributes redirectAttributes,
                       @RequestParam("name") String name, @RequestParam("password") String password
    ) throws Exception {

        if (userService.updateMySelf(authCurrentUser().getId(), name, password))
            redirectAttributes.addFlashAttribute("success", "Данные успешно обновлены!");
        else
            redirectAttributes.addFlashAttribute("error", "Ошибка обновления!");

        return "redirect:/account";
    }

    @DeleteMapping("delete")
    public String delete(RedirectAttributes redirectAttributes) throws Exception {

        if (userService.deleteMySelf(authCurrentUser().getId())) {
            SecurityContextHolder.clearContext();
            return "redirect:/";
        } else
            redirectAttributes.addFlashAttribute("error", "Не удалось удалить учётную запись!");

        return "redirect:/account";
    }
}
