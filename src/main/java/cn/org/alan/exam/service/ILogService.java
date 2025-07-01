package cn.org.alan.exam.service;

import cn.org.alan.exam.model.entity.Log;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


public interface ILogService {
    
    Log add(Log log);

    
    Page<Log> getPage(Integer pageNum, Integer pageSize);
}
