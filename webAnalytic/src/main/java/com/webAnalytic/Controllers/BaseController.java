package com.webAnalytic.Controllers;

import com.webAnalytic.Auxiliary.Config.Security.AuthUserDetails;
import com.webAnalytic.Domains.User;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

class PairObjectBindingResult {
    private final String name;
    private final BindingResult br;
    private final Object object;

    public BindingResult getBindingResult() {
        return br;
    }

    public Object getObject() {
        return object;
    }

    public String getName() {
        return name;
    }

    public PairObjectBindingResult(String name, Object object, BindingResult br) {
        this.name = name;
        this.br = br;
        this.object = object;
    }
}

public abstract class BaseController {

    protected static final String validatorPath = "org.springframework.validation.BindingResult.";

    /**
    * Performs redirect with BindingResult binding.
    * */
    void redirectBindingResults(RedirectAttributes redirectAttributes, PairObjectBindingResult... pairsObjectBr) {

        for (var pair : pairsObjectBr) {
            redirectAttributes.addFlashAttribute(validatorPath + pair.getName(),
                    pair.getBindingResult());
            redirectAttributes.addFlashAttribute(pair.getName(), pair.getObject());
        }
    }

    /**
     * Returns current logged-in user;
     */
    protected User authCurrentUser() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user instanceof AuthUserDetails)
            return ((AuthUserDetails) user).getUser();

        return null;
    }

}
