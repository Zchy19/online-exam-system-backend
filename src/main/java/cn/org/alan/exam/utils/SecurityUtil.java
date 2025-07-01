package cn.org.alan.exam.utils;

import cn.org.alan.exam.common.exception.ServiceRuntimeException;
import cn.org.alan.exam.utils.security.SysUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;


@Slf4j
public class SecurityUtil {

    
    public static Integer getUserId() {
        SysUserDetails user = (SysUserDetails) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return user.getUser().getId();
    }

    
    public static String getRole() {
        List<? extends GrantedAuthority> list = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().collect(java.util.stream.Collectors.toList());
        return list.get(0).toString();
    }

    
    public static Integer getRoleCode() {
        List<? extends GrantedAuthority> list = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().collect(java.util.stream.Collectors.toList());
        String roleName = list.get(0).toString();
        Integer roleCode;
        if ("role_admin".equals(roleName)) {
            roleCode = 3;
        } else if ("role_teacher".equals(roleName)) {
            roleCode = 2;
        } else if ("role_student".equals(roleName)) {
            roleCode = 1;
        } else {
            throw new ServiceRuntimeException("无法获取角色代码");
        }
        return roleCode;
    }

    
    public static Integer getGradeId() {
        SysUserDetails user = (SysUserDetails) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return user.getUser().getGradeId();
    }


}
