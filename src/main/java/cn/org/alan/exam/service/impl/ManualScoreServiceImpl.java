package cn.org.alan.exam.service.impl;

import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.mapper.*;
import cn.org.alan.exam.model.entity.*;
import cn.org.alan.exam.model.form.answer.CorrectAnswerFrom;
import cn.org.alan.exam.model.vo.answer.AnswerExamVO;
import cn.org.alan.exam.model.vo.answer.UncorrectedUserVO;
import cn.org.alan.exam.model.vo.answer.UserAnswerDetailVO;
import cn.org.alan.exam.service.IManualScoreService;
import cn.org.alan.exam.utils.ClassTokenGenerator;
import cn.org.alan.exam.utils.SecurityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;



@Service
public class ManualScoreServiceImpl extends ServiceImpl<ManualScoreMapper, ManualScore> implements IManualScoreService {

    @Resource
    private ExamMapper examMapper;
    @Resource
    private ExamGradeMapper examGradeMapper;
    @Resource
    private UserExamsScoreMapper userExamsScoreMapper;
    @Resource
    private ExamQuAnswerMapper examQuAnswerMapper;
    @Resource
    private ManualScoreMapper manualScoreMapper;
    @Resource
    private CertificateUserMapper certificateUserMapper;

    
    @Override
    public Result<List<UserAnswerDetailVO>> getDetail(Integer userId, Integer examId) {
        List<UserAnswerDetailVO> list = examQuAnswerMapper.selectUserAnswer(userId, examId);
        return Result.success("查询成功", list);
    }

    @Override
    @Transactional
    public Result<String> correct(List<CorrectAnswerFrom> correctAnswerFroms) {
        List<ManualScore> list = new ArrayList<>(correctAnswerFroms.size());
        AtomicInteger manualTotalScore = new AtomicInteger();
        correctAnswerFroms.forEach(correctAnswerFrom -> {

            
            LambdaQueryWrapper<ExamQuAnswer> wrapper = new LambdaQueryWrapper<ExamQuAnswer>()
                    .select(ExamQuAnswer::getId)
                    .eq(ExamQuAnswer::getExamId, correctAnswerFrom.getExamId())
                    .eq(ExamQuAnswer::getUserId, correctAnswerFrom.getUserId())
                    .eq(ExamQuAnswer::getQuestionId, correctAnswerFrom.getQuestionId());

            ManualScore manualScore = new ManualScore();
            manualScore.setExamQuAnswerId(examQuAnswerMapper.selectOne(wrapper).getId());
            manualScore.setScore(correctAnswerFrom.getScore());
            list.add(manualScore);
            manualTotalScore.addAndGet(correctAnswerFrom.getScore());
        });
        manualScoreMapper.insertList(list);

        
        CorrectAnswerFrom correctAnswerFrom = correctAnswerFroms.get(0);
        LambdaUpdateWrapper<UserExamsScore> userExamsScoreLambdaUpdateWrapper = new LambdaUpdateWrapper<UserExamsScore>()
                .eq(UserExamsScore::getExamId, correctAnswerFrom.getExamId())
                .eq(UserExamsScore::getUserId, correctAnswerFrom.getUserId())
                .set(UserExamsScore::getWhetherMark, 1)
                .setSql("user_score = user_score + " + manualTotalScore.get());
        userExamsScoreMapper.update(userExamsScoreLambdaUpdateWrapper);

        
        
        LambdaQueryWrapper<Exam> examWrapper = new LambdaQueryWrapper<Exam>()
                .select(Exam::getId, Exam::getCertificateId, Exam::getPassedScore)
                .eq(Exam::getId, correctAnswerFrom.getExamId());
        Exam exam = examMapper.selectOne(examWrapper);
        
        if (exam.getCertificateId() != null && exam.getCertificateId() > 0) {
            
            LambdaQueryWrapper<UserExamsScore> examsScoreWrapper = new LambdaQueryWrapper<UserExamsScore>()
                    .select(UserExamsScore::getId, UserExamsScore::getUserScore)
                    .eq(UserExamsScore::getExamId, correctAnswerFrom.getExamId())
                    .eq(UserExamsScore::getUserId, correctAnswerFrom.getUserId());
            UserExamsScore userExamsScore = userExamsScoreMapper.selectOne(examsScoreWrapper);
            
            if (userExamsScore.getUserScore() >= exam.getPassedScore()) {
                
                CertificateUser certificateUser = new CertificateUser();
                certificateUser.setUserId(correctAnswerFrom.getUserId());
                certificateUser.setExamId(correctAnswerFrom.getExamId());
                certificateUser.setCode(ClassTokenGenerator.generateClassToken(18));
                certificateUser.setCertificateId(exam.getCertificateId());
                certificateUserMapper.insert(certificateUser);
            }

        }
        return Result.success("批改成功");
    }

    @Override
    public Result<IPage<AnswerExamVO>> examPage(Integer pageNum, Integer pageSize, String examName) {

        Page<AnswerExamVO> page = new Page<>(pageNum, pageSize);
        
        List<AnswerExamVO> list = examMapper.selectMarkedList(page, SecurityUtil.getUserId(), SecurityUtil.getRole(), examName).getRecords();

        
        list.forEach(answerExamVO -> {
            
            answerExamVO.setClassSize(examGradeMapper.selectClassSize(answerExamVO.getExamId()));
            
            LambdaQueryWrapper<UserExamsScore> numberWrapper = new LambdaQueryWrapper<UserExamsScore>()
                    .eq(UserExamsScore::getExamId, answerExamVO.getExamId());
            answerExamVO.setNumberOfApplicants(userExamsScoreMapper.selectCount(numberWrapper).intValue());
            
            LambdaQueryWrapper<UserExamsScore> correctedWrapper = new LambdaQueryWrapper<UserExamsScore>()
                    .eq(UserExamsScore::getWhetherMark, 1)
                    .eq(UserExamsScore::getExamId, answerExamVO.getExamId());
            answerExamVO.setCorrectedPaper(userExamsScoreMapper.selectCount(correctedWrapper).intValue());
        });
        return Result.success(null, page);

    }

    @Override

    public Result<IPage<UncorrectedUserVO>> stuExamPage(Integer pageNum, Integer pageSize, Integer examId, String realName) {
        IPage<UncorrectedUserVO> page = new Page<>(pageNum, pageSize);
        page = userExamsScoreMapper.uncorrectedUser(page, examId, realName);
        return Result.success(null, page);
    }
}
