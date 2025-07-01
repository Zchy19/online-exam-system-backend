package cn.org.alan.exam.model.form.exam;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDateTime;


@Data
public class ExamUpdateForm {

    
    @NotBlank(message = "考试标题不能为空")
    @Size(min = 3, max = 20, message = "请输入3-20个字符的考试标题")
    private String title;

    
    @NotNull(message = "请设置考试时间,单位m")
    @Min(value=0,message = "请设置大于0的考试时长")
    private Integer examDuration;

    
    private Integer maxCount;

    
    @Min(value=0,message = "及格分数必须大于0")
    @NotNull(message = "及格分不能为空")
    private Integer passedScore;

    
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime startTime;

    
    @Future(message = "结束时间必须是一个必须是一个将来的日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime endTime;

    
    private String gradeIds;

    
    private Integer certificateId;

    
    @NotNull(message = "单选题分数不能为空")
    @Min(value = 0)
    private Integer radioScore;

    
    @NotNull(message = "多选题分数不能为空")
    @Min(value = 0)
    private Integer multiScore;


    
    @NotNull(message = "判断题分数不能为空")
    @Min(value = 0)
    private Integer judgeScore;

    
    @NotNull(message = "简答题分数不能为空")
    @Min(value = 0)
    private Integer saqScore;
}
