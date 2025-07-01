package cn.org.alan.exam.model.vo.grade;


import lombok.Data;


@Data
public class GradeVO {
    private static final long serialVersionUID = 1L;

    
    private Integer id;

    
    private String gradeName;

    
    private Integer userId;

    
    private String userName;

    
    private String code;

    
    private Integer gradeCount;
}
