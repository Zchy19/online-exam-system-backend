package cn.org.alan.exam.model.vo.answer;

import lombok.Data;


@Data
public class AnswerExamVO {
    
    private Integer examId;
    
    private String examTitle;
    
    private Integer neededMark;
    
    private Integer classSize;

    private Integer numberOfApplicants;
    
    private Integer correctedPaper;
}
