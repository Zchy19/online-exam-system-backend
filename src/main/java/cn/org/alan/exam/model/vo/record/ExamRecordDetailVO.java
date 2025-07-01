package cn.org.alan.exam.model.vo.record;

import cn.org.alan.exam.model.entity.Option;
import lombok.Data;

import java.util.List;


@Data
public class ExamRecordDetailVO {
    
    
    private String title;
    
    private String image;
    
    private List<Option> option;
    
    private String myOption;
    
    private String rightOption;
    
    private Integer isRight;
    
    private String analyse;
    
    private Integer quType;

}
