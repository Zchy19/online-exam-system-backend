package cn.org.alan.exam.model.form.answer;

import cn.org.alan.exam.common.group.AnswerGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class CorrectAnswerFrom {
    
    @NotBlank(message = "被批改人Id不能为空",groups = AnswerGroup.CorrectGroup.class)
    private Integer userId;
    
    @NotBlank(message = "试卷Id不能为空",groups = AnswerGroup.CorrectGroup.class)
    private Integer examId;
    
    @NotBlank(message = "试题Id不能为空",groups = AnswerGroup.CorrectGroup.class)
    private Integer questionId;
    
    @NotBlank(message = "分数不能为空",groups = AnswerGroup.CorrectGroup.class)
    private Integer score;
}
