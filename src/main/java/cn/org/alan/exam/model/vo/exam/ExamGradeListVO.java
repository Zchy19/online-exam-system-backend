package cn.org.alan.exam.model.vo.exam;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ExamGradeListVO {
    
    private Integer id;

    
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

    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
