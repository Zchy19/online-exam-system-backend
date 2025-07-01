package cn.org.alan.exam.service;

import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.model.entity.ExamQuAnswer;
import cn.org.alan.exam.model.vo.exam.ExamQuAnswerExtVO;
import cn.org.alan.exam.model.vo.score.QuestionAnalyseVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;


public interface IExamQuAnswerService extends IService<ExamQuAnswer> {

    
    Result<QuestionAnalyseVO> questionAnalyse(Integer examId, Integer questionId);

}
