package cn.org.alan.exam.controller;

import cn.org.alan.exam.common.group.UserGroup;
import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.model.form.auth.LoginForm;
import cn.org.alan.exam.model.form.user.UserForm;
import cn.org.alan.exam.service.IAuthService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Api(tags = "权限管理接口")
@RestController
@RequestMapping("/api/auths")
public class AuthController {


    @Resource
    private IAuthService iAuthService;

    @Value("${online-exam.login.captcha.enabled}")
    private boolean captchaEnabled;

    
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<String> login(HttpServletRequest request,
                                @Validated @RequestBody LoginForm loginForm) {
        return iAuthService.login(request, loginForm);
    }

    
    @ApiOperation("用户注销")
    @DeleteMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        return iAuthService.logout(request);
    }

    
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<String> register(HttpServletRequest request,
                                   @RequestBody @Validated(UserGroup.RegisterGroup.class) UserForm userForm) {
        return iAuthService.register(request, userForm);
    }

    
    @ApiOperation("获取图片验证码")
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {
        iAuthService.getCaptcha(request, response);
    }

    
    @ApiOperation("校验验证码")
    @PostMapping(value = {"/verifyCode/{code}", "/verifyCode/"})
    public Result<String> verifyCode(HttpServletRequest request, @PathVariable(value = "code", required = false) String code) {
        if (!captchaEnabled) {
            return Result.success();
        }
        return iAuthService.verifyCode(request, code);
    }

    
    @ApiOperation("记录学生登录时间")
    @PostMapping("/track-presence")
    public Result<String> trackPresence(HttpServletRequest request) {
        return iAuthService.sendHeartbeat(request);
    }

}
