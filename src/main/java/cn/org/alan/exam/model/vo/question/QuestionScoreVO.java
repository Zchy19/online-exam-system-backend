package cn.org.alan.exam.model.vo.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class QuestionScoreVO {

    
    @JsonProperty("题目ID")
    private String questionId;

    
    @JsonProperty("题目内容")
    private String questionContent;

    
    @JsonProperty("题目总分")
    private Integer totalScore;

    
    @JsonProperty("标准答案")
    private String qusetionAnswer;

    
    @JsonProperty("待评分答案")
    private String userAnswer;


}