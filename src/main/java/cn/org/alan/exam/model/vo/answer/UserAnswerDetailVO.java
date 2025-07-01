package cn.org.alan.exam.model.vo.answer;

import lombok.Data;


@Data
public class UserAnswerDetailVO {
    
    private Integer quId;
    
    private Integer userId;
    
    private Integer examId;
    
    private String quTitle;
    
    private String quImg;
    private String answer;
    private String refAnswer;
    private Integer correctScore;
    private String aiReason;
    private Integer totalScore;

}
