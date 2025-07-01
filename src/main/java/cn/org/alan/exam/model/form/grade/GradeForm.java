package cn.org.alan.exam.model.form.grade;

import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class GradeForm {
    
    @NotBlank
    private String gradeName;

    
    private String code;
}
