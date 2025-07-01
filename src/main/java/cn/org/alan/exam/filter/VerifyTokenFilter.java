package cn.org.alan.exam.filter;

import cn.org.alan.exam.model.entity.User;
import cn.org.alan.exam.utils.security.SysUserDetails;
import cn.org.alan.exam.utils.JwtUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;


@Slf4j
@Component
public class VerifyTokenFilter extends OncePerRequestFilter {
    
    @Resource
    private JwtUtil jwtUtil;
    
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ObjectMapper objectMapper;

    @Value("${jwt.expiration}")
    private long expiration;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String token = request.getHeader("Authorization");

        
        if (StringUtils.isBlank(token)) {
            
            filterChain.doFilter(request, response);
            
            return;
        }

        
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        String sessionId = request.getSession().getId();
        String storedToken = stringRedisTemplate.opsForValue().get("token:" + sessionId);
        if (StringUtils.isBlank(storedToken) || !token.equals(storedToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("token无效或已过期，请重新登录");
            return;
        }
        
        String refreshedToken = jwtUtil.verifyAndRefreshToken(token, storedToken);
        if (refreshedToken == null) {
            filterChain.doFilter(request, response);
            
            return;
        }
        
        if (!refreshedToken.equals(token)) {
            stringRedisTemplate.opsForValue().set("token:" + request.getSession().getId(), refreshedToken, expiration, TimeUnit.MILLISECONDS);
            response.setHeader("Authorization", "Bearer " + refreshedToken);
        }

        
        String userInfo = jwtUtil.getUser(refreshedToken);
        List<String> authList = jwtUtil.getAuthList(refreshedToken);

        
        User sysUser = objectMapper.readValue(userInfo, User.class);

        
        List<SimpleGrantedAuthority> permissions = authList.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        
        SysUserDetails securityUser = new SysUserDetails(sysUser);
        securityUser.setPermissions(permissions);

        
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(securityUser, null, permissions);

        
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        doFilter(request, response, filterChain);
    }
}
