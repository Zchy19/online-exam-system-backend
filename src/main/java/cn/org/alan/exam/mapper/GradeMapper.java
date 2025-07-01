package cn.org.alan.exam.mapper;

import cn.org.alan.exam.model.entity.Grade;
import cn.org.alan.exam.model.vo.grade.GradeVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface GradeMapper extends BaseMapper<Grade> {

    
    Page<GradeVO> selectGradePage(Page<GradeVO> page, Integer userId, String gradeName, Integer roleCode, List<Integer> gradeIdList);

    
    List<GradeVO> getAllGrade(Integer userId, Integer roleCode, List<Integer> gradeIdList);

    
    Grade getGradeByCode(String code);

}
