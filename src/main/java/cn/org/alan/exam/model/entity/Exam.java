package cn.org.alan.exam.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@ApiModel("考试实体类")
@TableName("t_exam")
public class Exam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("考试表ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("考试名称")
    private String title;

    @ApiModelProperty("考试时长")
    private Integer examDuration;

    @ApiModelProperty("及格分")
    private Integer passedScore;

    @ApiModelProperty("总分数")
    private Integer grossScore;

    @ApiModelProperty("最大切屏次数")
    private Integer maxCount;

    @ApiModelProperty("创建者ID")
    @TableField(fill = FieldFill.INSERT)
    private Integer userId;

    @ApiModelProperty("证书ID")
    private Integer certificateId;

    @ApiModelProperty("单选题数量")
    private Integer radioCount;

    
    @ApiModelProperty("单选题成绩")
    private Integer radioScore;

    @ApiModelProperty("多选题数量")
    private Integer multiCount;

    
    @ApiModelProperty("多选题成绩")
    private Integer multiScore;

    @ApiModelProperty("判断题数量")
    private Integer judgeCount;

    
    @ApiModelProperty("判断题成绩")
    private Integer judgeScore;

    @ApiModelProperty("简答题数量")
    private Integer saqCount;

    
    @ApiModelProperty("简答题成绩")
    private Integer saqScore;

    
    @ApiModelProperty("开始时间")
    private LocalDateTime startTime;

    
    @ApiModelProperty("结束时间")
    private LocalDateTime endTime;

    
    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    @ApiModelProperty("逻辑删除字段")
    private Integer isDeleted;

}
