package cn.org.alan.exam.mapper;

import cn.org.alan.exam.model.entity.UserDailyLoginDuration;
import cn.org.alan.exam.model.entity.UserExerciseRecord;
import cn.org.alan.exam.model.vo.stat.DailyVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.time.LocalDate;
import java.util.List;


public interface UserDailyLoginDurationMapper extends BaseMapper<UserDailyLoginDuration> {

    
    List<DailyVO> getDaily(Integer userId);

    
    UserDailyLoginDuration getTodayRecord(Integer userId, LocalDate date);
}
