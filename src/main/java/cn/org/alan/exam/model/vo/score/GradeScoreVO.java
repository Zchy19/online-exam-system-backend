package cn.org.alan.exam.model.vo.score;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class GradeScoreVO {
    private Integer id;
    
    private Integer examId;
    
    private Integer gradeId;
    private Integer passedScore;
    
    private String examTitle;
    
    private String gradeName;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    private Integer avgScore;
    
    private Integer maxScore;
    
    private Integer minScore;
    
    private Integer attendNum;
    
    private Integer absentNum;
    
    private Integer passedNum;
    
    private Integer totalNum;
    
    private Double passingRate;
}
