package cn.org.alan.exam.model.vo.exam;

import lombok.Data;


@Data
public class ExamQuAnswerExtVO {
    private static final long serialVersionUID = 1L;
    private Integer id;
    
    private Integer gradeId;
    
    private String image;

    
    private String content;
    
    private Integer sort;
}
