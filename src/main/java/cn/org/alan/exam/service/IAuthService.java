package cn.org.alan.exam.service;

import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.model.form.auth.LoginForm;
import cn.org.alan.exam.model.form.user.UserForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public interface IAuthService {

    
    Result<String> login(HttpServletRequest request, LoginForm loginForm);

    
    Result<String> logout(HttpServletRequest request);

    
    void getCaptcha(HttpServletRequest request, HttpServletResponse response);

    
    Result<String> verifyCode(HttpServletRequest request, String code);

    
    Result<String> register(HttpServletRequest request, UserForm userForm);

    
    Result<String> sendHeartbeat(HttpServletRequest request);
}