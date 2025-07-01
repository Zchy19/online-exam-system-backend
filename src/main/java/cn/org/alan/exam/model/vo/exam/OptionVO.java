package cn.org.alan.exam.model.vo.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class OptionVO {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    
    private Integer quId;


    
    private String image;

    
    @NotBlank(message = "选型内容(content)不能为空")
    private String content;

    private Boolean checkout;
    
    private Integer sort;
}
