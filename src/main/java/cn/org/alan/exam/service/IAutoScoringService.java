package cn.org.alan.exam.service;

import cn.org.alan.exam.model.entity.ExamQuAnswer;
import com.baomidou.mybatisplus.extension.service.IService;


public interface IAutoScoringService extends IService<ExamQuAnswer> {
    
    void autoScoringExam(Integer examId, Integer userId);
} 