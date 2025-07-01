package cn.org.alan.exam.model.vo.userbook;

import lombok.Data;


@Data
public class AddBookAnswerVO {
    
    private Integer correct;
    
    private String rightAnswers;
    
    private String analysis;
}
