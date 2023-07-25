package com.smart.smartcontactmanager.config;

import java.util.Collection;
import java.util.HashSet;
// import java.util.List;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.smart.smartcontactmanager.Entities.User;

// import antlr.collections.List;

public class CustomUserDetails implements UserDetails {

    private User user;
    
    

    public CustomUserDetails(User user) {
        super();
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        
        HashSet<SimpleGrantedAuthority> simpleGrantedAuthority = new HashSet<SimpleGrantedAuthority>();

        simpleGrantedAuthority.add(new SimpleGrantedAuthority(user.getRole()));
        return simpleGrantedAuthority;
    }

    @Override
    public String getPassword() {
        
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        
        return user.getEmail();
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
