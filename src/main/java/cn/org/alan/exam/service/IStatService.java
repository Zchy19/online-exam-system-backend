package cn.org.alan.exam.service;

import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.model.entity.ExamGrade;
import cn.org.alan.exam.model.vo.stat.AllStatsVO;
import cn.org.alan.exam.model.vo.stat.DailyVO;
import cn.org.alan.exam.model.vo.stat.GradeExamVO;
import cn.org.alan.exam.model.vo.stat.GradeStudentVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface IStatService extends IService<ExamGrade> {

    
    Result<List<GradeStudentVO>> getStudentGradeCount();

    
    Result<List<GradeExamVO>> getExamGradeCount();

    
    Result<AllStatsVO> getAllCount();

    
    Result<List<DailyVO>> getDaily();
}
