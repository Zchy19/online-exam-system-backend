package cn.org.alan.exam.model.vo.notice;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class NoticeVO {
    private Integer id;

    
    private String title;

    
    private String image;

    
    private String content;

    
    private Integer userId;

    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    
    private String realName;

    
    private Integer isPublic;

    
    private List<Integer> gradeIds;
}
