package com.webAnalytic.Controllers;

import com.webAnalytic.Config.Security.Entity.UserDetail;
import com.webAnalytic.Entity.User;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

class PairObjectBr {
    private final String name;
    private final BindingResult br;
    private final  Object object;

    public BindingResult getBr() {
        return br;
    }

    public Object getObject() {
        return object;
    }

    public String getName() {
        return name;
    }

    public PairObjectBr(String name,Object object, BindingResult br) {
        this.name = name;
        this.br = br;
        this.object = object;
    }
}

public abstract class BaseController {

    protected static final String validatorPath = "org.springframework.validation.BindingResult.";

    void redirectBindingResults(RedirectAttributes redirectAttributes, PairObjectBr... pairsObjectBr) {

        for (var pair : pairsObjectBr) {
            redirectAttributes.addFlashAttribute(validatorPath + pair.getName(),
                    pair.getBr());
            redirectAttributes.addFlashAttribute(pair.getName(), pair.getObject());
        }

    }

    /**
     * Returns current logged-in user;
     */
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

        return new ResponseEntity<>(out, result ? httpStatusSuccess : HttpStatus.INTERNAL_SERVER_ERROR);
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
