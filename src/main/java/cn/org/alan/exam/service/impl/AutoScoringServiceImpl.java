package cn.org.alan.exam.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.org.alan.exam.common.exception.ServiceRuntimeException;
import cn.org.alan.exam.mapper.ExamQuAnswerMapper;
import cn.org.alan.exam.model.entity.ExamQuAnswer;
import cn.org.alan.exam.service.IAuthService;
import cn.org.alan.exam.utils.agent.AIChat;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.org.alan.exam.model.vo.question.QuestionScoreVO;
import cn.org.alan.exam.service.IAutoScoringService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AutoScoringServiceImpl extends ServiceImpl<ExamQuAnswerMapper, ExamQuAnswer> implements IAutoScoringService {

    @Autowired
    private ExamQuAnswerMapper examQuAnswerMapper;

    @Autowired
    private AIChat aiChat;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Override
    @Async
    public void autoScoringExam(Integer examId, Integer userId) {
        int maxAttempts = 3; 
        long retryDelay = 5000; 

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

            
            TransactionStatus status = platformTransactionManager.getTransaction(def);
            try {
                
                List<QuestionScoreVO> questions = examQuAnswerMapper.getQuestionsForGrading(examId, userId);

                
                String scoringRequest = JSONUtil.toJsonStr(questions);

                
                String scoringResult = null; 
                String response = aiChat.getChatResponse(scoringRequest).trim();
                System.out.println(response);
                Pattern pattern = Pattern.compile("```json\\r?\\n(.*?)```", Pattern.DOTALL | Pattern.MULTILINE);
                Matcher matcher = pattern.matcher(response);
                if (matcher.find()) {
                    scoringResult = matcher.group(1).trim();
                } else {
                    throw new ServiceRuntimeException("JSON内容未匹配");
                }

                
                JSONArray scoreArray = JSONUtil
                        .parseArray(JSONUtil
                                .parseObj(scoringResult)
                                .getStr("评分结果"));

                
                for (int i = 0; i < scoreArray.size(); i++) {
                    JSONObject item = scoreArray.getJSONObject(i);
                    ExamQuAnswer examQuAnswer = new ExamQuAnswer();
                    examQuAnswer.setQuestionId(Integer.valueOf(item.getStr("题目ID")));
                    examQuAnswer.setAiScore(Integer.valueOf(item.getStr("最终得分")));
                    examQuAnswer.setAiReason(item.getStr("扣分原因"));

                    
                    LambdaQueryWrapper<ExamQuAnswer> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(ExamQuAnswer::getExamId, examId)
                            .eq(ExamQuAnswer::getUserId, userId)
                            .eq(ExamQuAnswer::getQuestionId, examQuAnswer.getQuestionId());

                    
                    ExamQuAnswer existingRecord = getOne(queryWrapper);
                    if (existingRecord != null) {
                        
                        examQuAnswer.setId(existingRecord.getId());
                        updateById(examQuAnswer);
                    } else {
                        
                        throw new ServiceRuntimeException("ai评分失败！");
                    }
                }

                
                platformTransactionManager.commit(status);
                return;
            } catch (Exception e) {
                
                platformTransactionManager.rollback(status);
                
                if (attempt == maxAttempts) {
                    throw new RuntimeException("ai评分重试多次后仍然失败！", e);
                }

                
                try {
                    TimeUnit.MILLISECONDS.sleep(retryDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("线程中断！", ie);
                }
            }
        }
    }
}