package cn.org.alan.exam.mapper;

import cn.org.alan.exam.model.entity.Exam;
import cn.org.alan.exam.model.vo.answer.AnswerExamVO;
import cn.org.alan.exam.model.vo.record.ExamRecordVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface ExamMapper extends BaseMapper<Exam> {

    
    IPage<AnswerExamVO> selectMarkedList(@Param("page") IPage<AnswerExamVO> page, @Param("userId") Integer userId, String role, String examName);

    
    Page<ExamRecordVO> getExamRecordPage(Page<ExamRecordVO> page, Integer userId, String examName, Boolean isASC);

    
    Page<ExamRecordVO> getTeacherExamRecordPage(Page<ExamRecordVO> page, Integer userId, String examName, Boolean isASC);

    
    Page<ExamRecordVO> getAllExamRecordPage(Page<ExamRecordVO> page, String examName, Boolean isASC);

}
