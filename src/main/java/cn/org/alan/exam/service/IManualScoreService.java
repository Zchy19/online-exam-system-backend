package cn.org.alan.exam.service;

import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.model.entity.ManualScore;
import cn.org.alan.exam.model.form.answer.CorrectAnswerFrom;
import cn.org.alan.exam.model.vo.answer.AnswerExamVO;
import cn.org.alan.exam.model.vo.answer.UncorrectedUserVO;
import cn.org.alan.exam.model.vo.answer.UserAnswerDetailVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface IManualScoreService extends IService<ManualScore> {
    
    Result<List<UserAnswerDetailVO>> getDetail(Integer userId, Integer examId);


    
    Result<String> correct(List<CorrectAnswerFrom> correctAnswerFroms);

    
    Result<IPage<AnswerExamVO>> examPage(Integer pageNum, Integer pageSize, String examName);

    
    Result<IPage<UncorrectedUserVO>> stuExamPage(Integer pageNum, Integer pageSize, Integer examId, String realName);

}
