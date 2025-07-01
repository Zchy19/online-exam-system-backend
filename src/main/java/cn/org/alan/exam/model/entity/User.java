package cn.org.alan.exam.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@ApiModel("用户实体类")
@TableName("t_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("密码")
    @TableField(fill = FieldFill.INSERT)
    private String password;

    @ApiModelProperty("头像地址")
    private String avatar;

    @ApiModelProperty("角色ID")
    @TableField(fill = FieldFill.INSERT)
    private Integer roleId;

    @ApiModelProperty("加入到班级ID")
    private Integer gradeId;

    
    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    
    @ApiModelProperty("状态")
    private Integer status;

    @TableLogic
    @ApiModelProperty("逻辑删除字段")
    private Integer isDeleted;

}
