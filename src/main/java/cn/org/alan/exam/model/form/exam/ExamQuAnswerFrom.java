package cn.org.alan.exam.model.form.exam;

import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class ExamQuAnswerFrom {
    
    private Integer examId;
    
    private Integer quId;
    
    @NotBlank
    private String answer;
}
