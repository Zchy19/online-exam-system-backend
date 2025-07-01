package cn.org.alan.exam.model.form.count;

import lombok.Data;


@Data
public class ClassCountResult {
    
    private Integer gradeId;
    
    private String gradeName;
    private Integer count;
    
    private int gradeCount;
    
    private  int examCount;
    
    private int questionCount;
}
