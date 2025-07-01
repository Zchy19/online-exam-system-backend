package cn.org.alan.exam.model.vo.question;

import cn.org.alan.exam.model.entity.Option;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class QuestionVO {
    private Integer id;
    
    private String content;
    
    private Integer repoId;
    
    private  String image;
    
    private String repoTitle;
    
    private Integer quType;
    private String analysis;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    private List<Option> options;
}
