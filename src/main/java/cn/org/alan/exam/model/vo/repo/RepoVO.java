package cn.org.alan.exam.model.vo.repo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class RepoVO {
    
    private Integer id;
    
    private Integer userId;
    
    private String title;
    
    private Integer isExercise;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    private String realName;
    
    private Integer categoryId;
    
    private String categoryName;
    
    private Integer questionCount;
}
