package cn.org.alan.exam.model.vo.record;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
public class ExerciseRecordVO {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    
    @TableField(fill = FieldFill.INSERT)
    private Integer userId;

    
    @NotNull(message = "题库名不能为空")
    private String title;

    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;


}
