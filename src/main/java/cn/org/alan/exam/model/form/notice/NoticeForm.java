package cn.org.alan.exam.model.form.notice;

import cn.org.alan.exam.common.result.Result;
import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class NoticeForm {

    
    private Integer id;

    
    @NotBlank
    private String title;

    
    private String image;

    
    @NotBlank
    private String content;

    
    private Integer userId;

    
    private String gradeIds;

    
    private Integer isPublic;


}
