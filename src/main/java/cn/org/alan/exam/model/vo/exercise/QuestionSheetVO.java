package cn.org.alan.exam.model.vo.exercise;

import lombok.Data;
import lombok.experimental.Accessors;


@Data
public class QuestionSheetVO {
    
    private Integer quId;
    
    private Integer quType;
    
    private Integer repoId;
    private Integer exercised;
    private Integer isRight;
}
