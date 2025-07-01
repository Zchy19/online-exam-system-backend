package cn.org.alan.exam.model.vo.exam;

import lombok.Data;


@Data
public class ExamDetailRespVO {
    private Integer id;
    
    private Integer examId;

    
    private Integer questionId;
    
    private Integer score;
    
    private Integer sort;
    
    private Integer type;
}
