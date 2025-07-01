package cn.org.alan.exam.config;

import cn.org.alan.exam.filter.VerifyTokenFilter;
import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.utils.ResponseUtil;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity 
@EnableGlobalMethodSecurity(prePostEnabled = true) 
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    
    @Resource
    private ResponseUtil responseUtil;

    
    @Resource
    private VerifyTokenFilter verifyTokenFilter;

    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        
        http.cors().and().csrf().disable();

        
        http.authorizeRequests()
                
                .antMatchers(
                        
                        "/api/auths/**",
                        
                        "/webjars/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/v2/api-docs",
                        "/swagger-resources/configuration/ui",
                        "/swagger-resources/configuration/security",
                        
                        "/doc.html",
                        "/ws/**",
                        "/ws-app/**"
                )
                
                .permitAll()
                
                .anyRequest().authenticated();

        
        http.exceptionHandling()
                .accessDeniedHandler((request, response, accessDeniedException) ->
                        
                        responseUtil.response(response, Result.failed("你没有该资源的访问权限"))
                );

        
        http.formLogin().disable();

        
        http.addFilterBefore(verifyTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}