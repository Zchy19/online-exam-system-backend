package cn.org.alan.exam.mapper;

import cn.org.alan.exam.model.entity.Option;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;


public interface OptionMapper extends BaseMapper<Option> {

    
    Integer insertBatch(List<Option> options);

    
    List<Option> selectAllByQuestionId(Integer id);

    
    List<Option> selectOptionByqId(Integer id);

}
