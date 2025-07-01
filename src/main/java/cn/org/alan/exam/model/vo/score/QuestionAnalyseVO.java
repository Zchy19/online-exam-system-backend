package cn.org.alan.exam.model.vo.score;

import lombok.Data;




@Data
public class QuestionAnalyseVO {
    
    private Integer rightCount;
    
    private Integer totalCount;
    private Double accuracy;

}
