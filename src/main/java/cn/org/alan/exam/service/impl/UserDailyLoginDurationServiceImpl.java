package cn.org.alan.exam.service.impl;

import cn.org.alan.exam.mapper.UserDailyLoginDurationMapper;
import cn.org.alan.exam.mapper.UserExamsScoreMapper;
import cn.org.alan.exam.model.entity.UserDailyLoginDuration;
import cn.org.alan.exam.model.entity.UserExamsScore;
import cn.org.alan.exam.service.IUserDailyLoginDurationService;
import cn.org.alan.exam.service.IUserExamsScoreService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class UserDailyLoginDurationServiceImpl extends ServiceImpl<UserDailyLoginDurationMapper, UserDailyLoginDuration> implements IUserDailyLoginDurationService {
}
