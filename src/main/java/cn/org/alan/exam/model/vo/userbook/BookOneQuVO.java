package cn.org.alan.exam.model.vo.userbook;

import cn.org.alan.exam.model.entity.Option;
import lombok.Data;

import java.util.List;


@Data
public class BookOneQuVO {
    private static final long serialVersionUID = 1L;

    
    private String image;

    
    private String content;

    
    private Integer quType;

    
    private List<Option> answerList;
}
