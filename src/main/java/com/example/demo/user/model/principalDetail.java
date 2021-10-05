package com.example.demo.user.model;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Data
public class principalDetail implements UserDetails {
    private uservo uservo;

    public principalDetail(uservo uservo){
        this.uservo=uservo;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority>roles=new ArrayList<>();
        roles.add(new GrantedAuthority(){
            @Override
            public String getAuthority() {
                System.out.println(uservo.getRole()+"권한 가져오기");
                return uservo.getRole();
            }
        });
        return roles;
    }

    @Override
    public String getPassword() {
        
        return uservo.getPwd();
    }

    @Override
    public String getUsername() {
        
        return uservo.getEmail();
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
