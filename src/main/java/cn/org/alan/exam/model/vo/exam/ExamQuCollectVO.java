package cn.org.alan.exam.model.vo.exam;

import cn.org.alan.exam.model.entity.ExamQuAnswer;
import cn.org.alan.exam.model.entity.Option;
import lombok.Data;

import java.util.List;


@Data
public class ExamQuCollectVO {
    
    private Integer id;
    
    private String image;
    
    private String title;
    
    private List<Option> option;
    
    private String myOption;

    
    private Integer quType;
}
