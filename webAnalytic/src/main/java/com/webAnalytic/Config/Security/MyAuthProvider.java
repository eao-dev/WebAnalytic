package com.webAnalytic.Config.Security;

import com.webAnalytic.Config.Security.Entity.UserDetail;
import com.webAnalytic.DAO.DAO;
import com.webAnalytic.Entity.User;
import com.webAnalytic.Utils.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class MyAuthProvider implements AuthenticationProvider {

    private DAO<User> userDAO;

    @Autowired
    public void setUserDAO(DAO<User> userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Authenticates the user on the system; If success then set authentication token,
     * otherwise, throw an exception with message of an error;
     *
     * @param authentication - object {@link Authentication}
     */
    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String userName = authentication.getName();
        String password = authentication.getCredentials().toString();
        final String loginErrorMessage = "Login failure!";

        try {
            // Find user
            User user = userDAO.getByObject(new User(userName));
            if (user == null) throw new BadCredentialsException(loginErrorMessage);

            // Check password
            if (!Arrays.equals(Hash.doHash(password), user.getPassword()))
                throw new BadCredentialsException(loginErrorMessage);

            // Create new principal
            UserDetail principal = new UserDetail(user);

            // Auth success
            return new UsernamePasswordAuthenticationToken(principal, password, principal.getAuthorities());

        } catch (Exception ex) {
            throw new BadCredentialsException(loginErrorMessage);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}