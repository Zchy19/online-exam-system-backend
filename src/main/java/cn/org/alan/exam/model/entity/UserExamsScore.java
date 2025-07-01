package cn.org.alan.exam.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@ApiModel("用户考试分数记录实体类")
@TableName("t_user_exams_score")
public class UserExamsScore implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户考试成绩表ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户id")
    @TableField(fill = FieldFill.INSERT)
    private Integer userId;

    @ApiModelProperty("试卷id")
    private Integer examId;

    @ApiModelProperty("总时长")
    private Integer totalTime;

    @ApiModelProperty("用户用时")
    private Integer userTime;

    @ApiModelProperty("用户得分")
    private Integer userScore;

    
    @ApiModelProperty("交卷时间")
    private LocalDateTime limitTime;

    @ApiModelProperty("切屏次数")
    private Integer count;

    
    @ApiModelProperty("状态")
    private Integer state;

    
    @ApiModelProperty("是否阅卷")
    private Integer whetherMark;

    
    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
