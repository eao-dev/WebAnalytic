package com.webAnalytic.Auxiliary.Config.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
* Security configuration;
* */

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthProvider myAuthProvider;

    @Value("${loginPage}")
    public String loginPage;

    @Value("${logoutPage}")
    public String logoutPage;

    @Value("${allRolesAccess}")
    private String[] allRolesAccess;

    @Value("${adminAccess}")
    private String[] adminAccess;

    @Value("${userAccess}")
    private String[] userAccess;

    @Value("${allUsers}")
    private String[] allUsers;

    @Value("${anonymous}")
    private String[] anonymous;

    @Value("${disabledCSRFURL}")
    private String[] disabledCSRFURL;

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
        http.authorizeRequests()
                .antMatchers(anonymous).anonymous()
                .antMatchers(adminAccess).hasAuthority(UserRole.ADMIN.toString())
                .antMatchers(userAccess).hasAuthority(UserRole.USER.toString())
                .antMatchers(allUsers).permitAll()
                .antMatchers(allRolesAccess).authenticated()
                .anyRequest().authenticated().and().formLogin()
                .loginPage(loginPage)
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/")
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher(logoutPage))
                .logoutSuccessUrl(loginPage)
                .permitAll()
                .and().csrf()
                .ignoringAntMatchers(disabledCSRFURL)   // Disabled CSRF
        ;
    }

}