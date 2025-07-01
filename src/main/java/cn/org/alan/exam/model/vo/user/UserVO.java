package cn.org.alan.exam.model.vo.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class UserVO {

    private Integer id;
    
    private String userName;
    
    private String realName;
    
    private Integer roleId;
    
    private String password;
    
    private String avatar;
    
    private String gradeName;
    
    private Integer gradeId;
    
    private Integer userId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private String status;

}
