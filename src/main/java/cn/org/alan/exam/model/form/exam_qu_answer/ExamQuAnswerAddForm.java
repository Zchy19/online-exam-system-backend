package cn.org.alan.exam.model.form.exam_qu_answer;

import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class ExamQuAnswerAddForm {
    
    private Integer examId;
    
    private Integer quId;
    
    @NotBlank
    private String answer;
}
