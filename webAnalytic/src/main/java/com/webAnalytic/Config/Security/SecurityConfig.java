package com.webAnalytic.Config.Security;

import com.webAnalytic.Config.Security.Entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.annotation.PostConstruct;
import java.util.*;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyAuthProvider myAuthProvider;

    private final List<String> permissionsAllRoles = new ArrayList<>();
    private final List<String> permissionsAll = new ArrayList<>();
    private final List<String> permissionsAnonymous = new ArrayList<>();

    private final Map<UserRole, List<String>> permissionsRoles = new HashMap<>();

    private final List<String> disableCSRF = new ArrayList<>();

    static private final Map<UserRole, String> mapRoles = new HashMap<>();

    public static Map<UserRole, String> getMapRoles() {
        return mapRoles;
    }

    /**
     * Init map which contains user roles.
     */
    private void initRoles() {
        // Init arrays for URL
        for (var role : UserRole.values()) {
            permissionsRoles.put(role, new ArrayList<>());
            mapRoles.put(role, role.toString());
        }
    }

    /**
     * Add permissions for any role.
     *
     * @param role   - name of role;
     * @param uriArr - array string contains URL.
     */
    private void addPermissionRole(UserRole role, String... uriArr) {
        List<String> arrList = permissionsRoles.get(role);
        arrList.addAll(Arrays.asList(uriArr));
    }

    /**
     * Add permissions for all user roles.
     *
     * @param uriArr - array string contains URL.
     */
    private void addPermissionAllRoles(String... uriArr) {
        permissionsAllRoles.addAll(Arrays.asList(uriArr));
    }

    /**
     * Add permissions for all users.
     *
     * @param uriArr - array string contains URL.
     */
    private void addPermissionAll(String... uriArr) {
        permissionsAll.addAll(Arrays.asList(uriArr));
    }


    /**
     * Add permissions for anonymous users.
     *
     * @param uriArr - array string contains URL.
     */
    private void addPermissionAnonymous(String... uriArr) {
        permissionsAnonymous.addAll(Arrays.asList(uriArr));
    }

    /**
     * Disabled CSRF for specified uri.
     *
     * @param uriArr - array string contains URL.
     */
    private void disableCSRF(String... uriArr) {
        disableCSRF.addAll(Arrays.asList(uriArr));
    }

    /**
     * Assigning permissions for roles, anonymous and all.
     */
    @PostConstruct
    private void setPermissions() {

        initRoles();

        /* Roles settings */
        addPermissionAllRoles(
                "/",
                "/reports/**",
                "/analytics/**");

        // Only for admin
        addPermissionRole(UserRole.ADMIN,
                "/userManagement/**",
                "/account/**");

        // Only for user
        //addPermissionRole(UserRole.USER);

        /* All users */
        addPermissionAll(
                "/collector/**",
                "/style/**",
                "/images/**",
                "/app/**",
                "/fontawesome/**");

        /* Anonymous users */
        addPermissionAnonymous("/account/registration");

        /* Disable CSRF */
        disableCSRF("/collector/**");
    }

    /**
     * Authority settings.
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(myAuthProvider); // Add custom auth provider
    }

    /**
     * Authorization settings.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        /* Anonymous */
        http.authorizeRequests().
                antMatchers(permissionsAnonymous.toArray(new String[0])).anonymous();

        /* UserRole */
        for (var item : permissionsRoles.entrySet()) {
            http.authorizeRequests().antMatchers(item.getValue().toArray(new String[0])). // Add URL's
                    hasAuthority(mapRoles.get(item.getKey()));                            // Add role
        }

        /* Other */
        http.authorizeRequests().
                antMatchers(permissionsAll.toArray(new String[0])).permitAll().          // All users
                antMatchers(permissionsAllRoles.toArray(new String[0])).authenticated(). // All user roles
                anyRequest().authenticated().and().formLogin();

        /* Disabled CSRF */
        for (var url : disableCSRF)
            http.csrf().ignoringAntMatchers(url);
    }

}