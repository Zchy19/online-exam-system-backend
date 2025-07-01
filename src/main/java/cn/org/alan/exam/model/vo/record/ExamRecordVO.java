package cn.org.alan.exam.model.vo.record;

import cn.org.alan.exam.model.entity.Option;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class ExamRecordVO {

    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    
    private String title;

    
    private Integer examDuration;

    
    private Integer passedScore;

    
    private Integer grossScore;

    
    private Integer maxCount;

    
    @TableField(fill = FieldFill.INSERT)
    private Integer userId;

    
    private Integer certificateId;

    
    private Integer radioCount;

    
    private Integer radioScore;

    
    private Integer multiCount;

    
    private Integer multiScore;

    
    private Integer judgeCount;

    
    private Integer judgeScore;

    
    private Integer saqCount;

    
    private Integer saqScore;

    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    
    private Integer userTime;

    
    private Integer userScore;

    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime limitTime;



}
