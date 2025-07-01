package cn.org.alan.exam.model.form.user;

import cn.org.alan.exam.common.group.UserGroup;
import cn.org.alan.exam.utils.excel.ExcelImport;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;



@Data
public class UserForm {
    
    private Integer id;

    
    private LocalDateTime createTime;

    
    @NotBlank(groups = {UserGroup.CreateUserGroup.class, UserGroup.RegisterGroup.class}, message = "用户名不能为空")
    
    @ExcelImport(value = "用户名*",unique = true,required = true)
    private String userName;

    
    @NotBlank(groups = UserGroup.RegisterGroup.class,message = "密码不能为空")
    private String password;

    
    @NotBlank(groups = {UserGroup.CreateUserGroup.class, UserGroup.RegisterGroup.class}, message = "真实姓名不能为空")
    @ExcelImport(value = "真实姓名*")
    private String realName;

    
    @ExcelImport(value = "角色")
    private Integer roleId;

    
    private Integer gradeId;

    
    @NotBlank(groups = {UserGroup.UpdatePasswordGroup.class}, message = "原密码不能为空")
    private String originPassword;

    
    @NotBlank(groups = {UserGroup.UpdatePasswordGroup.class}, message = "新密码不能为空")
    private String newPassword;

    
    @NotBlank(groups = {UserGroup.UpdatePasswordGroup.class, UserGroup.RegisterGroup.class}, message = "校验密码不能为空")
    private String checkedPassword;

}
