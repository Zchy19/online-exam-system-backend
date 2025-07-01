package cn.org.alan.exam.mapper;

import cn.org.alan.exam.model.entity.Grade;
import cn.org.alan.exam.model.vo.stat.GradeExamVO;
import cn.org.alan.exam.model.vo.stat.GradeStudentVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface StatMapper extends BaseMapper<Grade> {

    
    List<GradeStudentVO> StudentGradeCount(@Param("roleId") Integer roleId, Integer id, List<Integer> gradeIdList);

    
    List<GradeExamVO> ExamGradeCount(@Param("roleId") Integer roleId, Integer id, List<Integer> gradeIdList);

}
