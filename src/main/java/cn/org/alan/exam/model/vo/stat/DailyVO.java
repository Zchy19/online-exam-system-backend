package cn.org.alan.exam.model.vo.stat;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDate;


@Data
public class DailyVO {
    private Integer id;

    
    private Integer userId;

    
    private LocalDate loginDate;

    
    private Integer totalSeconds;
}
