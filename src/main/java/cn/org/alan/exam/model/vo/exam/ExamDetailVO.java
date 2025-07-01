package cn.org.alan.exam.model.vo.exam;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ExamDetailVO {

    
    private Integer id;
    
    private String username;
    
    private String title;

    
    private Integer examDuration;

    
    private Integer passedScore;

    
    private Integer grossScore;

    
    private Integer maxCount;

    
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

    
    private LocalDateTime startTime;

    
    private LocalDateTime endTime;

}
