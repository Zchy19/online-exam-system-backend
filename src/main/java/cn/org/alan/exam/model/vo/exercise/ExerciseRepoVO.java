package cn.org.alan.exam.model.vo.exercise;

import lombok.Data;


@Data
public class ExerciseRepoVO {
    private Integer id;
    
    private String repoTitle;
    
    private Integer totalCount;
    private Integer exerciseCount;
    
    private Integer categoryId;
    private String categoryName;
    private Integer parentCategoryId;
    private String parentCategoryName;
}
