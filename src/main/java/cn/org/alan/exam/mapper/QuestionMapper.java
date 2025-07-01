package cn.org.alan.exam.mapper;

import cn.org.alan.exam.model.entity.Question;
import cn.org.alan.exam.model.vo.question.QuestionVO;
import cn.org.alan.exam.model.vo.exercise.QuestionSheetVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;


public interface QuestionMapper extends BaseMapper<Question> {

    
    QuestionVO selectSingle(Integer id);

    
    List<QuestionSheetVO> selectQuestionSheet(Integer repoId, Integer quType, Integer userId);

    
    QuestionVO selectDetail(Integer id);

    
    IPage<QuestionVO> selectQuestionPage(IPage<QuestionVO> page, Integer userId, Integer roleCode, String title, Integer type, Integer repoId);

}
