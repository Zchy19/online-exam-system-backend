package cn.org.alan.exam.model.vo.exam;

import cn.org.alan.exam.model.entity.Option;
import lombok.Data;

import java.util.List;


@Data
public class ExamQuDetailVO {
    private static final long serialVersionUID = 1L;

    
    private String image;

    
    private String content;
    
    private List<OptionVO> answerList;
    
    private Integer quType;
    
    private Integer sort;

}
