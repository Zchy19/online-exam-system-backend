package cn.org.alan.exam.model.vo.userbook;

import cn.org.alan.exam.model.entity.Option;
import lombok.Data;

import java.util.List;


@Data
public class UserExamBookVO {
    
    private String content;
    
    private List<Option> answerList;
    
    private String rightAnswers;
    
    private String analyse;
    
    private String reply;

}
