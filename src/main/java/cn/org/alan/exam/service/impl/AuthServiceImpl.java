package cn.org.alan.exam.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.org.alan.exam.common.exception.ServiceRuntimeException;
import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.converter.UserConverter;
import cn.org.alan.exam.mapper.RoleMapper;
import cn.org.alan.exam.mapper.UserDailyLoginDurationMapper;
import cn.org.alan.exam.mapper.UserMapper;
import cn.org.alan.exam.model.entity.Log;
import cn.org.alan.exam.model.entity.User;
import cn.org.alan.exam.model.entity.UserDailyLoginDuration;
import cn.org.alan.exam.model.form.auth.LoginForm;
import cn.org.alan.exam.model.form.user.UserForm;
import cn.org.alan.exam.service.IAuthService;
import cn.org.alan.exam.service.ILogService;
import cn.org.alan.exam.utils.*;
import cn.org.alan.exam.utils.security.SysUserDetails;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



@Service
public class AuthServiceImpl implements IAuthService {
    private static final String HEARTBEAT_KEY_PREFIX = "user:heartbeat:";
    private static final long HEARTBEAT_INTERVAL_MILLIS = 10 * 60 * 1000; 

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private UserConverter userConverter;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private UserDailyLoginDurationMapper userDailyLoginDurationMapper;
    @Value("${online-exam.login.captcha.enabled}")
    private boolean captchaEnabled;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    private ILogService logService;
    @Value("${jwt.expiration}")
    private long expiration;

    
    @SneakyThrows(JsonProcessingException.class)
    @Override
    public Result<String> login(HttpServletRequest request, LoginForm loginForm) {
        
        String s = stringRedisTemplate.opsForValue().get("isVerifyCode" + request.getSession().getId());
        if (StringUtils.isBlank(s) && captchaEnabled) {
            throw new ServiceRuntimeException("请先验证验证码");
        }
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserName, loginForm.getUsername());
        User user = userMapper.selectOne(wrapper);
        
        if (Objects.isNull(user)) {
            throw new ServiceRuntimeException("该用户不存在");
        }
        if (user.getIsDeleted() == 1) {
            throw new ServiceRuntimeException("该用户已注销");
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String userPassword = SecretUtils.desEncrypt(loginForm.getPassword());
        if (!encoder.matches(userPassword, user.getPassword())) {
            throw new ServiceRuntimeException("密码错误");
        }
        user.setPassword(null);
        
        List<String> permissions = roleMapper.selectCodeById(user.getRoleId());

        
        List<SimpleGrantedAuthority> userPermissions = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority("role_" + permission)).collect(java.util.stream.Collectors.toList());

        
        SysUserDetails sysUserDetails = new SysUserDetails(user);
        
        sysUserDetails.setPermissions(userPermissions);
        
        String userInfo = objectMapper.writeValueAsString(user);
        
        String token = jwtUtil.createJwt(userInfo, userPermissions.stream().map(String::valueOf).collect(java.util.stream.Collectors.toList()));
        
        stringRedisTemplate.opsForValue().set("token:" + request.getSession().getId(), token, expiration, TimeUnit.MILLISECONDS);

        
        
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(sysUserDetails, user.getPassword(), userPermissions);

        
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        
        
        stringRedisTemplate.delete("isVerifyCode" + request.getSession().getId());

        
        String device = httpServletRequest.getHeader("User-Agent");
        String ipRegion = IPUtils.getIPRegion(httpServletRequest);
        Log log = Log.builder()
                .place(ipRegion)
                .device(extractDeviceType(device))
                .behavior("设备登录")
                .userId(user.getId()).build();
        logService.add(log);
        return Result.success("登录成功", token);
    }


    
    public static String extractDeviceType(String userAgent) {
        
        String pattern = "\\((.*?);";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(userAgent);
        if (m.find()) {
            
            return m.group(1);
        }
        return null;
    }

    
    @Override
    public Result<String> logout(HttpServletRequest request) {
        
        HttpSession session = request.getSession(false);
        String token = request.getHeader("Authorization");
        if (StringUtils.isNotBlank(token) && session != null) {
            
            String device = httpServletRequest.getHeader("User-Agent");
            String ipRegion = IPUtils.getIPRegion(httpServletRequest);
            Log log = Log.builder()
                    .place(ipRegion)
                    .device(extractDeviceType(device))
                    .behavior("设备登出")
                    .userId(SecurityUtil.getUserId()).build();
            logService.add(log);
            token = token.substring(7);
            stringRedisTemplate.delete("token:" + request.getSession().getId());
            session.invalidate();
        }
        return Result.success("退出成功");
    }

    
    @SneakyThrows(IOException.class)
    @Override
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {
        
        LineCaptcha captcha = CaptchaUtil
                .createLineCaptcha(200, 100, 4, 300);

        
        
        String code = captcha.getCode();
        String key = "code" + request.getSession().getId();
        stringRedisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
        
        response.setContentType("image/jpeg");
        ServletOutputStream os = response.getOutputStream();
        captcha.write(os);
        os.close();
    }

    
    @Override
    public Result<String> verifyCode(HttpServletRequest request, String code) {
        String key = "code" + request.getSession().getId();
        String rightCode = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(rightCode)) {
            throw new ServiceRuntimeException("验证码已过期");
        }
        if (!rightCode.equalsIgnoreCase(code)) {
            throw new ServiceRuntimeException("验证码错误");
        }
        
        stringRedisTemplate.delete(key);
        
        stringRedisTemplate.opsForValue().set("isVerifyCode" + request.getSession().getId(), "1", 5, TimeUnit.MINUTES);
        return Result.success("验证码校验成功");
    }

    
    @Override
    public Result<String> register(HttpServletRequest request, UserForm userForm) {
        
        String s = stringRedisTemplate.opsForValue().get("isVerifyCode" + request.getSession().getId());
        if (StringUtils.isBlank(s)) {
            throw new ServiceRuntimeException("请先验证验证码");
        }
        
        if (!SecretUtils.desEncrypt(userForm.getPassword()).equals(SecretUtils.desEncrypt(userForm.getCheckedPassword()))) {
            throw new ServiceRuntimeException("两次密码不一致");
        }
        User user = userConverter.fromToEntity(userForm);
        user.setPassword(new BCryptPasswordEncoder().encode(SecretUtils.desEncrypt(user.getPassword())));
        user.setRoleId(1);
        userMapper.insert(user);
        
        stringRedisTemplate.delete("isVerifyCode" + request.getSession().getId());
        return Result.success("注册成功");
    }

    
    @Override
    public Result<String> sendHeartbeat(HttpServletRequest request) {
        
        String key = HEARTBEAT_KEY_PREFIX + SecurityUtil.getUserId();
        if (SecurityUtil.getRoleCode() == 1) {
            
            String lastHeartbeatStr = stringRedisTemplate.opsForValue().get(key);
            
            LocalDateTime utcTime = LocalDateTime.now(ZoneOffset.UTC);
            LocalDateTime now = utcTime.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
            
            stringRedisTemplate.opsForValue().set(key, now.toString());
            LocalDateTime lastHeartbeat = null;
            
            if (lastHeartbeatStr == null) {
                lastHeartbeat = now;
            } else {
                lastHeartbeat = LocalDateTime.parse(lastHeartbeatStr);
            }
            
            Duration durationSinceLastHeartbeat = Duration.between(lastHeartbeat, now);
            
            LocalDate date = DateTimeUtil.getDate();
            
            
            Integer userId = SecurityUtil.getUserId();
            UserDailyLoginDuration userDailyLogin = userDailyLoginDurationMapper.getTodayRecord(userId, date);
            
            if (Objects.isNull(userDailyLogin)) {
                
                UserDailyLoginDuration userDailyLoginDuration = new UserDailyLoginDuration();
                
                userDailyLoginDuration.setUserId(userId);
                
                userDailyLoginDuration.setLoginDate(date);
                
                userDailyLoginDuration.setTotalSeconds((int) durationSinceLastHeartbeat.getSeconds());
                userDailyLoginDurationMapper.insert(userDailyLoginDuration);
            } else {
                
                
                userDailyLogin.setTotalSeconds(userDailyLogin.getTotalSeconds()
                        + (int) durationSinceLastHeartbeat.getSeconds());
                userDailyLoginDurationMapper.updateById(userDailyLogin);
            }
        }
        return Result.success("请求成功");
    }
}
