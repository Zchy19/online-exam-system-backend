package cn.org.alan.exam.model.form.exercise;


import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
public class ExerciseFillAnswerFrom {
    
    @NotNull(message = "题库Id不能为空")
    private Integer repoId;
    
    @NotNull(message = "试题Id不能为空")
    private Integer quId;
    
    @NotBlank(message = "作答内容不能为空")
    private String answer;
    
    @NotNull(message = "试题类型不能为空")
    @Min(value = 1, message = "试题类型最小值应为1")
    @Max(value = 4, message = "试题类型最大值应为4")
    private Integer quType;
}
