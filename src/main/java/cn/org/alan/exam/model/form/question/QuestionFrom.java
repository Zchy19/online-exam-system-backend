package cn.org.alan.exam.model.form.question;

import cn.org.alan.exam.common.group.QuestionGroup;
import cn.org.alan.exam.model.entity.Option;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Data
public class QuestionFrom {

    private Integer id;

    
    @NotNull(message = "试题类型(quType)不能为空", groups = QuestionGroup.QuestionAddGroup.class)
    @Min(value = 1, message = "试题类型(quType)只能是：1单选2多选3判断4简答", groups = QuestionGroup.QuestionAddGroup.class)
    @Max(value = 4, message = "试题类型(quType)只能是：1单选2多选3判断4简答", groups = QuestionGroup.QuestionAddGroup.class)
    private Integer quType;

    
    private String image;
    private String analysis;

    
    @NotBlank(message = "题干(content)不能为空", groups = QuestionGroup.QuestionAddGroup.class)
    private String content;

    
    private LocalDateTime createTime;

    
    @NotNull(message = "题库id(repoId)不能为空", groups = QuestionGroup.QuestionAddGroup.class)
    private Integer repoId;

    
    private List<Option> options;

}
