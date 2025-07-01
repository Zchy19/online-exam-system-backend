package cn.org.alan.exam.model.vo.certificate;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class MyCertificateVO {
    
    private String code;
    
    private Integer id;

    
    private String certificateName;

    
    private String image;

    
    private String certificationNuit;

    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    private String examName;
    
    private Integer examId;

}
