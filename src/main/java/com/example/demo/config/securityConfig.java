package com.example.demo.config;

import com.example.demo.config.webconfig.corsconfig;
import com.example.demo.filters.authorizationFilter;
import com.example.demo.filters.loginFilter;
import com.example.demo.jwt.service.jwtService;
import com.example.demo.user.model.userDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.RequiredArgsConstructor;

@Configuration//빈등록: 스프링 컨테이너에서 객체에서 관리
@EnableWebSecurity/////필터를 추가해준다
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class securityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private corsconfig corsconfig;
    @Autowired
    private jwtService jwtService;
    @Autowired
    private userDao userDao;

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
    @Bean
    public BCryptPasswordEncoder pwdEncoder() {
       return  new BCryptPasswordEncoder();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilter(corsconfig.crosfilter())
            .addFilter(new loginFilter(jwtService))
            .addFilter(new authorizationFilter(authenticationManager(),jwtService,userDao))
            .csrf().disable().formLogin().disable().httpBasic().disable()
            .authorizeRequests().antMatchers("/api/**").authenticated().anyRequest().permitAll();

    }
}
