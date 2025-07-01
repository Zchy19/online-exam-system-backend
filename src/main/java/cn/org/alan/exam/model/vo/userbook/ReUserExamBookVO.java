package cn.org.alan.exam.model.vo.userbook;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ReUserExamBookVO {
    private static final long serialVersionUID = 1L;

    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    
    private Integer examId;

    
    @TableField(fill = FieldFill.INSERT)
    private Integer userId;

    
    private Integer quId;

    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
