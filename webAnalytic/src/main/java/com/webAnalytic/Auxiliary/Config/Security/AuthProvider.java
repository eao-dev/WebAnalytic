package com.webAnalytic.Auxiliary.Config.Security;

import com.webAnalytic.Domains.DAO.DAO;
import com.webAnalytic.Domains.User;
import com.webAnalytic.Auxiliary.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Custom authorization provider.
 **/

@Component
public class AuthProvider implements AuthenticationProvider {

    private DAO<User> userDAO;

    @Autowired
    public void setUserDAO(DAO<User> userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Returns user {@link User} from DB by username.
     * */
    User getUser(String userName) {
        try {
            return userDAO.getByObject(new User(userName));
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Returns hash of password;
     * @param passwordString - string contains password.
     * @return bytes array contains hash of password if success otherwise null.
     * */
    byte[] getHash(final String passwordString) {
        try {
            return Hash.doHash(passwordString);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Authenticates the user in the system; If successful, an authentication token is set,
     * otherwise an exception is thrown with an error message.
     *
     * @param authentication - object {@link Authentication}
     */
    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        final String loginErrorMessage = "Login failure!";
        var credentials = authentication.getCredentials();

        User user = getUser(authentication.getName());
        if (user != null) {
            if (Arrays.equals(getHash(credentials.toString()), user.getPassword())) {
                AuthUserDetails principal = new AuthUserDetails(user);
                return new UsernamePasswordAuthenticationToken(principal, credentials, principal.getAuthorities());
            }
        }
        throw new BadCredentialsException(loginErrorMessage); // Invalid credentials
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}