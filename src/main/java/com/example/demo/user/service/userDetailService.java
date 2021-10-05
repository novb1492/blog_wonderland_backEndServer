package com.example.demo.user.service;

import com.example.demo.user.model.userDao;
import com.example.demo.user.model.principalDetail;
import com.example.demo.user.model.uservo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class userDetailService implements UserDetailsService {
    @Autowired
    private userDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername");
        System.out.println("로그인 시도 email "+username);
        uservo uservo=userDao.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("존재하지 않는 이메일입니다"));
        return new principalDetail(uservo);
    }
    
}
