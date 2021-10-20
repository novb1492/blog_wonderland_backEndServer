package com.example.demo.user.service;

import com.example.demo.user.model.userDao;
import com.example.demo.user.model.principalDetail;
import com.example.demo.user.model.uservo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class userDetailService implements UserDetailsService {
    private final static Logger LOGGER=LoggerFactory.getLogger(userDetailService.class);
    @Autowired
    private userDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.info("loadUserByUsername");
        LOGGER.info("로그인 시도 email "+username);
        uservo uservo=userDao.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("존재하지 않는 이메일입니다"));
        return new principalDetail(uservo);
    }
    
}
