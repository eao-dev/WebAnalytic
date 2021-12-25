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

    private final String loginErrorMessage = "Ошибка авторизации";
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

        try {
            // Find input user
            User user = userDAO.getByObject(new User(userName));
            if (user == null)
                throw new Exception(String.format("пользователь с именем \"%s\" не найден!", userName));

            // Hash password
            var bytePasswordHash = Hash.doHash(password);

            // Check password
            if (!Arrays.equals(bytePasswordHash, user.getPassword()))
                throw new Exception("неверный пароль");

            UserDetail principal = new UserDetail(user);

            // Auth success
            return new UsernamePasswordAuthenticationToken(principal, password, principal.getAuthorities());

        } catch (Exception ex) {
            throw new BadCredentialsException(String.format("%s: %s", loginErrorMessage, ex.getMessage()));
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}