package cn.org.alan.exam.mapper;

import cn.org.alan.exam.model.entity.Exam;
import cn.org.alan.exam.model.entity.ExamGrade;
import cn.org.alan.exam.model.vo.exam.ExamGradeListVO;
import cn.org.alan.exam.model.vo.score.GradeScoreVO;
import cn.org.alan.exam.model.vo.stat.GradeExamVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface ExamGradeMapper extends BaseMapper<ExamGrade> {

    
    Integer addExamGrade(Integer examId, List<Integer> gradeIds);

    
    Integer selectClassSize(Integer id);

    
    IPage<ExamGradeListVO> selectClassExam(IPage<ExamGradeListVO> examPage, Integer userId, String title, Boolean isASC);

    
    IPage<ExamGradeListVO> selectAdminClassExam(IPage<ExamGradeListVO> examPage, Integer userId, String title, Boolean isASC);

}
