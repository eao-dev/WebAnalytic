package com.webAnalytic.Auxiliary.Config.Security;

import com.webAnalytic.Domains.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class contains information about authenticated user.
 * */

public class AuthUserDetails implements UserDetails {

    private final User user;
    private final UserRole role;

    private final List<GrantedAuthority> authorities = new ArrayList<>();

    public AuthUserDetails(User user) {
        this.user = user;

        // Set role (user or admin)
        this.role = user.isAdmin() ? UserRole.ADMIN : UserRole.USER;

        // Set privileges for current user
        authorities.add(new SimpleGrantedAuthority(role.toString()));
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    public String getName() {
        return user.getName();
    }

    public UserRole getRole() {
        return role;
    }

    public String getLogin() {
        return user.getLogin();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return user.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
