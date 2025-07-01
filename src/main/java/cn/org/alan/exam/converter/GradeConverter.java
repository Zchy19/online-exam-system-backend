package cn.org.alan.exam.converter;

import cn.org.alan.exam.model.entity.Grade;
import cn.org.alan.exam.model.form.grade.GradeForm;
import cn.org.alan.exam.model.vo.grade.GradeVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Mapper(componentModel="spring")
public interface GradeConverter {

    Page<GradeVO> pageEntityToVo(Page<Grade> page);

    Grade formToEntity(GradeForm gradeForm);

    List<GradeVO> listEntityToVo(List<Grade> page);
    GradeVO  GradeToGradeVO(Grade grade);

}
