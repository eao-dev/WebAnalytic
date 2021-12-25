package com.webAnalytic.Config.Security.Entity;

import com.webAnalytic.Entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static com.webAnalytic.Config.Security.SecurityConfig.getMapRoles;

public class UserDetail implements UserDetails {

    private final User user;

    private final List<GrantedAuthority> authorities = new ArrayList<>();

    public User getUser() {
        return user;
    }

    public UserDetail(User user) {
        this.user = user;
        // Set privileges for current user
        authorities.add(new SimpleGrantedAuthority(getMapRoles().get(getRole())));
    }

    public Long getId() {
        return user.getId();
    }

    public String getName() {
        return user.getName();
    }

    public UserRole getRole(){
        return user.isAdmin()?UserRole.ADMIN:UserRole.USER;
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
