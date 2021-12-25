package com.webAnalytic.Controllers;

import com.webAnalytic.Config.Security.Entity.UserDetail;
import com.webAnalytic.Entity.User;
import com.webAnalytic.Entity.WebSite;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class BaseController {

    protected static final String validatorPath = "org.springframework.validation.BindingResult.";

    @ModelAttribute("newUser")
    private User newUser() {
        return new User();
    }

    @ModelAttribute("newWebSite")
    private WebSite newWebSite(){
        return new WebSite();
    }

    /**
     * Returns current logged-in user;
     */
    @ModelAttribute("userAuth")
    protected User authCurrentUser() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user instanceof UserDetail)
            return ((UserDetail) user).getUser();

        return null;
    }

    /**
     * Returns string contains JSON-object which contain action status.
     *
     * @param status  - boolean status variable;
     * @param message - message for current status;
     */
    private static String jsonStatusObject(boolean status, String message) {
        JSONObject statusEditObj = new JSONObject();
        statusEditObj.put("message", message);
        statusEditObj.put("status", status);
        return new JSONObject().put("actionStatus", statusEditObj).toString();
    }

    /**
     * Returns object {@link ResponseEntity<String>} contains status and message.
     * Set into returns object specified HTTP-code;
     *
     * @param result            - status;
     * @param statusSuccessMsg  - message of success;
     * @param statusErrorMsg    - message of error;
     * @param httpStatusSuccess - this status returns when operation successful,
     *                          otherwise set 500(HttpStatus.INTERNAL_SERVER_ERROR)
     */
    protected ResponseEntity<String> outResult(boolean result, String statusSuccessMsg, String statusErrorMsg,
                                               HttpStatus httpStatusSuccess)
            throws Exception {
        String out;

        if (!result)
            out = jsonStatusObject(false, statusErrorMsg);
        else
            out = jsonStatusObject(true, statusSuccessMsg);

        return new ResponseEntity<>(out, result?httpStatusSuccess:HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Returns object {@link ResponseEntity<String>} contains JSON object and set HTTP-code 200(OK);
     *
     * @param object - object {@link JSONObject};
     */
    protected ResponseEntity<String> outObject(JSONObject object) {
        return new ResponseEntity<>(object.toString(), HttpStatus.OK);
    }

}
