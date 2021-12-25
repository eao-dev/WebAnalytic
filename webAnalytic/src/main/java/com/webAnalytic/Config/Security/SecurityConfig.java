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

    private final List<String> permissionsAll = new ArrayList<>();
    private final List<String> permissionsAnonymous = new ArrayList<>();
    private final Map<String, String> permissionsGroup = new HashMap<>();
    private final List<String> disableCSRF = new ArrayList<>();

    static private final Map<UserRole, String> mapRoles = new HashMap<>();

    public static Map<UserRole, String> getMapRoles() {
        return mapRoles;
    }


    /**
     * Init map which contains user-groups
     */
    static void initRoles() {
        mapRoles.put(UserRole.USER, "user");
        mapRoles.put(UserRole.ADMIN, "admin");
    }

    /**
     * Add permissions for any role;
     *
     * @param role   - name of role;
     * @param uriArr - array string contains uri;
     */
    private void addPermissionRole(UserRole role, String... uriArr) {
        String groupStr = mapRoles.get(role);
        for (var uri : uriArr)
            permissionsGroup.put(uri, groupStr);
    }

    /**
     * Add permissions for all users;
     *
     * @param uriArr - array string contains uri;
     */
    private void addPermissionAll(String... uriArr) {
        permissionsAll.addAll(Arrays.asList(uriArr));
    }

    /**
     * Add permissions for anonymous users;
     *
     * @param uriArr - array string contains uri;
     */
    private void addPermissionAnonymous(String... uriArr) {
        permissionsAnonymous.addAll(Arrays.asList(uriArr));
    }


    /**
     *
     * Disabled CSRF for specified uri;
     *
     * @param uriArr - array string contains uri;
     * */
    private void disableCSRF(String... uriArr){
        disableCSRF.addAll(Arrays.asList(uriArr));
    }


    /**
     * Assigning permissions for roles, anonymous and all
     */
    @PostConstruct
    private void setPermissions() {
        initRoles();

        // Admin
        addPermissionRole(UserRole.ADMIN,
                "/account/**",
                "/userManagement/**",
                "/websiteManagement/add**",
                "/websiteManagement/remove**",
                "/websiteManagement/clearStat**");

        // User
        addPermissionRole(UserRole.USER,
                "/webSiteManagement/",
                "/webSiteManagement/statistic**",
                "/analytics.js");

        // Anonymous
        addPermissionAnonymous("/account/registration**");

        // All
        addPermissionAll(
                "/collector/**",
                "/style/**",
                "/images/**",
                "/app/**",
                "/fontawesome/**");

        // Disable CSRF
        disableCSRF("/collector/**");
    }

    /**
     * Authority settings
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(myAuthProvider); // Add custom auth provider
    }

    /**
     * Authorization settings
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        for (var uri : permissionsAll)
            http.authorizeRequests().antMatchers(uri).permitAll();

        for (var uri : permissionsAnonymous)
            http.authorizeRequests().antMatchers(uri).anonymous();

        for (var it : permissionsGroup.entrySet())
            http.authorizeRequests().antMatchers(it.getKey()).hasAuthority(it.getValue());

        http.cors().and().
                authorizeRequests().
                anyRequest().
                authenticated().
                and().
                formLogin();

        for (var uri: disableCSRF)
            http.csrf().ignoringAntMatchers(uri);

    }

}