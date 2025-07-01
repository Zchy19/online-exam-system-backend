package cn.org.alan.exam.mapper;

import cn.org.alan.exam.model.entity.ManualScore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;


public interface ManualScoreMapper extends BaseMapper<ManualScore> {

    
    Integer insertList(List<ManualScore> manualScores);

}
