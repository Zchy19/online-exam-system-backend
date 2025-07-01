package cn.org.alan.exam.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
@ApiModel("考试试题答案关联实体类")
@TableName("t_exam_qu_answer")
public class ExamQuAnswer implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("考试记录答案ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("考试ID")
    private Integer examId;

    @ApiModelProperty("试题ID")
    private Integer questionId;

    @ApiModelProperty("题目类型")
    private Integer questionType;

    
    @ApiModelProperty("答案ID")
    private String answerId;

    
    @ApiModelProperty("答案内容")
    private String answerContent;

    
    @ApiModelProperty("是否选中")
    private Integer checkout;

    
    @ApiModelProperty("是否标记")
    private Integer isSign;

    
    @ApiModelProperty("是否正确")
    private Integer isRight;

    
    @ApiModelProperty("是否正确")
    private Integer aiScore;

    
    @ApiModelProperty("ai评分原因")
    private String aiReason;
}
