package cn.org.alan.exam.model.vo.record;

import cn.org.alan.exam.model.entity.Option;
import lombok.Data;

import java.util.List;


@Data
public class ExerciseRecordDetailVO {
    
    private String title;

    
    private List<Option> option;

    
    private String myOption;

    
    private String rightOption;

    
    private Integer isRight;

    
    private String analyse;

    
    private String image;

    
    private Integer quType;
}
