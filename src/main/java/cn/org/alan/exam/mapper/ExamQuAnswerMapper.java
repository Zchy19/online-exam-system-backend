package cn.org.alan.exam.mapper;

import cn.org.alan.exam.model.entity.ExamQuAnswer;
import cn.org.alan.exam.model.vo.question.QuestionScoreVO;
import cn.org.alan.exam.model.vo.answer.UserAnswerDetailVO;
import cn.org.alan.exam.model.vo.score.QuestionAnalyseVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface ExamQuAnswerMapper extends BaseMapper<ExamQuAnswer> {

    
    QuestionAnalyseVO questionAnalyse(Integer examId, Integer questionId);

    
    List<UserAnswerDetailVO> selectUserAnswer(Integer userId, Integer examId);

    
    List<QuestionScoreVO> getQuestionsForGrading(Integer examId, Integer userId);
}
