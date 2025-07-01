package cn.org.alan.exam.model.vo.exam;

import cn.org.alan.exam.model.entity.ExamQuestion;
import lombok.Data;

import java.util.Calendar;
import java.util.List;


@Data
public class ExamQuestionListVO {
    
    private List<ExamQuestionVO> radioList;
    
    private List<ExamQuestionVO> multiList;
    
    private List<ExamQuestionVO> judgeList;
    
    private List<ExamQuestionVO> saqList;
    private Integer examDuration;
    public Long leftSeconds;
}
