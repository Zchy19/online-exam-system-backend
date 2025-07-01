package cn.org.alan.exam.mapper;

import cn.org.alan.exam.model.entity.ExamQuestion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;


public interface ExamQuestionMapper extends BaseMapper<ExamQuestion> {

    
    int insertQuestion(Integer examId, Integer quType, Integer quScore, List<Map<String, Object>> questionIdsAndSorts);

    
    int insertSingleQuestion(Integer examId, Integer quType, Integer quScore, Map<String, Object> questionIdsAndSorts);


    
    List<ExamQuestion> getExamQuByExamIdAndQuType(Integer examId, Integer quType);


    
    List<ExamQuestion> getUnansweredSaqQuestions(Integer examId, Integer userId);
}
