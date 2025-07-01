package cn.org.alan.exam.service.impl;

import cn.org.alan.exam.common.exception.ServiceRuntimeException;
import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.converter.ExamConverter;
import cn.org.alan.exam.converter.ExamQuAnswerConverter;
import cn.org.alan.exam.mapper.*;
import cn.org.alan.exam.model.entity.*;
import cn.org.alan.exam.model.form.exam.ExamAddForm;
import cn.org.alan.exam.model.form.exam.ExamUpdateForm;
import cn.org.alan.exam.model.form.exam_qu_answer.ExamQuAnswerAddForm;
import cn.org.alan.exam.model.vo.exam.*;
import cn.org.alan.exam.model.vo.record.ExamRecordDetailVO;
import cn.org.alan.exam.service.IAutoScoringService;
import cn.org.alan.exam.service.IExamService;
import cn.org.alan.exam.service.IOptionService;
import cn.org.alan.exam.service.IQuestionService;
import cn.org.alan.exam.utils.ClassTokenGenerator;
import cn.org.alan.exam.utils.SecurityUtil;
import com.aliyun.oss.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements IExamService {

    @Resource
    private ExamMapper examMapper;
    @Resource
    private ExamConverter examConverter;
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private IQuestionService questionService;
    @Resource
    private ExamGradeMapper examGradeMapper;
    @Resource
    private ExamRepoMapper examRepoMapper;
    @Resource
    private ExamQuestionMapper examQuestionMapper;
    @Resource
    private OptionMapper optionMapper;
    @Resource
    private IOptionService optionService;
    @Resource
    private ExamQuAnswerMapper examQuAnswerMapper;
    @Resource
    private UserExamsScoreMapper userExamsScoreMapper;
    @Resource
    private UserBookMapper userBookMapper;
    @Resource
    private ExamQuAnswerConverter examQuAnswerConverter;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CertificateUserMapper certificateUserMapper;
    @Resource
    private IAutoScoringService autoScoringService;

    @Override
    @Transactional
    public Result<String> createExam(ExamAddForm examAddForm) {
        
        Exam exam = examConverter.formToEntity(examAddForm);
        
        
        int grossScore = examAddForm.getRadioCount() * examAddForm.getRadioScore()
                + examAddForm.getMultiCount() * examAddForm.getMultiScore()
                + examAddForm.getJudgeCount() * examAddForm.getJudgeScore()
                + examAddForm.getSaqCount() * examAddForm.getSaqScore();
        exam.setGrossScore(grossScore);
        
        int examRows = examMapper.insert(exam);
        if (examRows < 1) {
            throw new ServiceRuntimeException("添加考试到数据库失败!");
        }
        
        
        String gradeIdsStr = examAddForm.getGradeIds();
        List<Integer> gradeIds = Arrays.stream(gradeIdsStr.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        
        Integer gradeRows = examGradeMapper.addExamGrade(exam.getId(), gradeIds);
        if (gradeRows < 1) {
            throw new ServiceRuntimeException("创建失败!");
        }
        
        Integer repoId = examAddForm.getRepoId();
        ExamRepo examRepo = new ExamRepo();
        examRepo.setExamId(exam.getId());
        examRepo.setRepoId(repoId);
        
        int examRepoRows = examRepoMapper.insert(examRepo);
        if (examRepoRows < 1) {
            throw new ServiceRuntimeException("创建失败!");
        }

        
        Map<Integer, Integer> quTypeToScore = new HashMap<>();
        quTypeToScore.put(1, exam.getRadioScore());
        quTypeToScore.put(2, exam.getMultiScore());
        quTypeToScore.put(3, exam.getJudgeScore());
        quTypeToScore.put(4, exam.getSaqScore());
        
        Map<Integer, Integer> quTypeToCount = new HashMap<>();
        quTypeToCount.put(1, exam.getRadioCount());
        quTypeToCount.put(2, exam.getMultiCount());
        quTypeToCount.put(3, exam.getJudgeCount());
        quTypeToCount.put(4, exam.getSaqCount());
        int sortCounter = 0;
        
        if("0".equals(examAddForm.getAddQuype())){

            if(StringUtils.isBlank(examAddForm.getQuIds())){
                throw new ServiceException("自己选题的时候不能不选试题");
            }
            Integer examId = exam.getId();
            
            List<String> selectedQuIdStrings = Arrays.asList(examAddForm.getQuIds().split(","));
            List<Integer> selectedQuIds = selectedQuIdStrings.stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            
            List<Question> selectedQuestions = questionMapper.selectBatchIds(selectedQuIds);
            if (selectedQuestions.size() != selectedQuIds.size()) {
                
                
                
            }

            
            Map<Integer, List<Question>> groupedQuestions = new LinkedHashMap<>(); 
            groupedQuestions.put(1, new ArrayList<>()); 
            groupedQuestions.put(2, new ArrayList<>()); 
            groupedQuestions.put(3, new ArrayList<>()); 
            groupedQuestions.put(4, new ArrayList<>()); 

            
            Map<Integer, Question> questionMap = selectedQuestions.stream()
                    .collect(Collectors.toMap(Question::getId, q -> q));

            for (Integer quId : selectedQuIds) {
                Question question = questionMap.get(quId);
                if (question != null && groupedQuestions.containsKey(question.getQuType())) {
                    groupedQuestions.get(question.getQuType()).add(question);
                } else {

                }
            }


            
            for (Map.Entry<Integer, List<Question>> entry : groupedQuestions.entrySet()) {
                Integer quType = entry.getKey();
                List<Question> questionsInGroup = entry.getValue();
                Integer quScore = quTypeToScore.get(quType); 

                if (quScore == null) {

                    
                    continue; 
                }

                for (Question question : questionsInGroup) {
                    Map<String, Object> detail = new HashMap<>();
                    detail.put("questionId", question.getId());
                    detail.put("sort", sortCounter);
                    sortCounter++; 

                    
                    int examQueRows = examQuestionMapper.insertSingleQuestion(examId, quType, quScore, detail);
                    if (examQueRows < 1) {
                        
                        throw new ServiceRuntimeException("创建考试失败，插入题目关联时出错, Question ID: " + question.getId());
                    }
                }
            }
        }
        
        if("1".equals(examAddForm.getAddQuype())){
            
            for (Map.Entry<Integer, Integer> entry : quTypeToCount.entrySet()) {
                Map<Integer, Integer> questionSortMap = new HashMap<>();
                
                Integer quType = entry.getKey();
                Integer count = entry.getValue();
                Integer examId = exam.getId();
                Integer quScore = quTypeToScore.get(quType);
                
                LambdaQueryWrapper<Question> typeQueryWrapper = new LambdaQueryWrapper<>();
                typeQueryWrapper.select(Question::getId)
                        .eq(Question::getQuType, quType)
                        .eq(Question::getIsDeleted, 0)
                        .eq(Question::getRepoId, examAddForm.getRepoId());
                List<Question> questionsByType = questionMapper.selectList(typeQueryWrapper);
                if (questionsByType.size() < count) {
                    throw new ServiceRuntimeException("题库中类型为" + quType + "的题目数量不足" + count + "个！");
                }
                List<Integer> typeQuestionIds = questionsByType.stream().map(Question::getId).collect(Collectors.toList());
                Collections.shuffle(typeQuestionIds);
                List<Integer> sampledIds = typeQuestionIds.subList(0, count);
                
                if (!sampledIds.isEmpty()) {
                    for (Integer qId : sampledIds) {
                        questionSortMap.put(qId, sortCounter); 
                        sortCounter++; 
                    }
                    
                    List<Map<String, Object>> questionDetails = new ArrayList<>();
                    for (Map.Entry<Integer, Integer> sortEntry : questionSortMap.entrySet()) {
                        Map<String, Object> detail = new HashMap<>();
                        detail.put("questionId", sortEntry.getKey());
                        detail.put("sort", sortEntry.getValue());
                        questionDetails.add(detail);
                    }
                    
                    int examQueRows = examQuestionMapper.insertQuestion(examId, quType, quScore, questionDetails);
                    if (examQueRows < 1) {
                        throw new ServiceRuntimeException("创建考试失败");
                    }
                }

            }
        }
        return Result.success("创建考试成功");
    }

    
    public Integer getGrossScore(Exam exam) {
        Integer grossScore = 0;
        try {
            grossScore = exam.getRadioCount() * exam.getRadioScore()
                    + exam.getMultiCount() * exam.getMultiScore()
                    + exam.getJudgeCount() * exam.getJudgeScore()
                    + exam.getSaqCount() * exam.getSaqScore();
        } catch (Exception e) {
            throw new ServiceRuntimeException("计算总分时出现空指针异常:" + e.getMessage());
        }
        return grossScore;
    }

    @Override
    @Transactional
    public Result<String> updateExam(ExamUpdateForm examUpdateForm, Integer examId) {
        
        Integer userId = SecurityUtil.getUserId();
        
        Exam examTemp = this.getById(examId);
        
        Integer grossScore = getGrossScore(examTemp);
        
        Exam exam = examConverter.formToEntity(examUpdateForm);
        exam.setId(examId);
        
        exam.setGrossScore(grossScore);
        
        Integer resultRow = examMapper.updateById(exam);
        if (resultRow < 1) {
            throw new ServiceRuntimeException("修改试卷失败");
        }
        return Result.success("修改试卷成功");
    }

    @Override
    @Transactional
    public Result<String> deleteExam(String ids) {
        
        List<Integer> examIds = Arrays.stream(ids.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        
        int row = examMapper.deleteBatchIds(examIds);
        if (row < 1) {
            throw new ServiceRuntimeException("删除失败，删除考试表时失败");
        }
        return Result.success("删除试卷成功");
    }

    @Override
    public Result<IPage<ExamVO>> getPagingExam(Integer pageNum, Integer pageSize, String title) {
        
        Page<Exam> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<Exam> examQuery = new LambdaQueryWrapper<>();
        examQuery.like(StringUtils.isNotBlank(title), Exam::getTitle, title)
                .eq(Exam::getIsDeleted, 0);
        if (SecurityUtil.getRoleCode() == 2) {
            examQuery.eq(Exam::getUserId, SecurityUtil.getUserId());
        }
        Page<Exam> examPage = examMapper.selectPage(page, examQuery);
        
        Page<ExamVO> examVOPage = examConverter.pageEntityToVo(examPage);
        return Result.success("查询成功", examVOPage);
    }

    @Override
    public Result<ExamQuestionListVO> getQuestionList(Integer examId) {
        Integer userId = SecurityUtil.getUserId();
        
        if (!isUserTakingExam(examId)) {
            return Result.failed("考试在进行");
        }
        ExamQuestionListVO examQuestionListVO = new ExamQuestionListVO();
        
        Exam byId = this.getById(examId);
        examQuestionListVO.setExamDuration(byId.getExamDuration());
        Calendar cl = Calendar.getInstance();
        LocalDateTime createTime = getUserStarExamTime(examId, userId);
        if (createTime != null) {
            Date date = Date.from(createTime.atZone(ZoneId.systemDefault()).toInstant());
            cl.setTime(date);
        } else {
            return Result.failed("错误");
        }
        cl.add(Calendar.MINUTE, byId.getExamDuration());
        examQuestionListVO.setLeftSeconds((cl.getTimeInMillis() - System.currentTimeMillis()) / 1000);
        
        for (Integer quType = 1; quType <= 4; quType++) {
            
            List<ExamQuestion> examQuestionList = examQuestionMapper.getExamQuByExamIdAndQuType(examId, quType);
            
            List<ExamQuestionVO> examQuestionVOS = examConverter.examQuestionListEntityToVO(examQuestionList);
            
            for (ExamQuestionVO temp : examQuestionVOS) {
                LambdaQueryWrapper<ExamQuAnswer> examQuAnswerLambdaQueryWrapper = new LambdaQueryWrapper<>();
                examQuAnswerLambdaQueryWrapper.eq(ExamQuAnswer::getQuestionId, temp.getQuestionId())
                        .eq(ExamQuAnswer::getExamId, examId)
                        .eq(ExamQuAnswer::getUserId, userId);
                List<ExamQuAnswer> examQuAnswers = examQuAnswerMapper.selectList(examQuAnswerLambdaQueryWrapper);
                if (examQuAnswers.size() > 0) {
                    temp.setCheckout(true);
                } else {
                    temp.setCheckout(false);
                }
            }
            if (examQuestionVOS.isEmpty()) {
                continue;
            }
            
            if (quType == 1) {
                
                examQuestionListVO.setRadioList(examQuestionVOS);
            } else if (quType == 2) {
                
                examQuestionListVO.setMultiList(examQuestionVOS);
            } else if (quType == 3) {
                
                examQuestionListVO.setJudgeList(examQuestionVOS);
            } else if (quType == 4) {
                
                examQuestionListVO.setSaqList(examQuestionVOS);
            }
        }
        return Result.success("查询成功", examQuestionListVO);
    }

    @Override
    public Result<ExamQuDetailVO> getQuestionSingle(Integer examId, Integer quId) {
        
        if (!isUserTakingExam(examId)) {
            return Result.failed("没有考试在进行");
        }
        ExamQuDetailVO examQuDetailVO = new ExamQuDetailVO();
        LambdaQueryWrapper<ExamQuestion> examQuestionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        examQuestionLambdaQueryWrapper.eq(ExamQuestion::getQuestionId, quId)
                .eq(ExamQuestion::getExamId, examId);
        ExamQuestion examQuestion = examQuestionMapper.selectOne(examQuestionLambdaQueryWrapper);
        examQuDetailVO.setSort(examQuestion.getSort());
        
        Question quById = questionService.getById(quId);
        
        examQuDetailVO.setImage(quById.getImage());
        examQuDetailVO.setContent(quById.getContent());
        examQuDetailVO.setQuType(quById.getQuType());
        
        LambdaQueryWrapper<Option> optionLambdaQuery = new LambdaQueryWrapper<>();
        optionLambdaQuery.eq(Option::getQuId, quId);
        List<Option> list = optionMapper.selectList(optionLambdaQuery);
        List<OptionVO> optionVOS = examConverter.opListEntityToVO(list);
        for (OptionVO temp : optionVOS) {

            LambdaQueryWrapper<ExamQuAnswer> examQuAnswerLambdaQueryWrapper = new LambdaQueryWrapper<>();
            examQuAnswerLambdaQueryWrapper.eq(ExamQuAnswer::getQuestionId, temp.getQuId())
                    .eq(ExamQuAnswer::getExamId, examId)
                    .eq(ExamQuAnswer::getUserId, SecurityUtil.getUserId());
            List<ExamQuAnswer> examQuAnswers = examQuAnswerMapper.selectList(examQuAnswerLambdaQueryWrapper);

            if (examQuAnswers.size() > 0) {
                for (ExamQuAnswer temp1 : examQuAnswers) {
                    Integer questionType = temp1.getQuestionType();
                    String answerId = temp1.getAnswerId();
                    String answerContent = temp1.getAnswerContent();
                    String idstr = temp.getId().toString();
                    switch (questionType) {
                        case 1:
                        case 3:
                            if (answerId.equals(idstr)) {
                                temp.setCheckout(true);
                            } else {
                                temp.setCheckout(false);
                            }
                            break;
                        case 2:
                            
                            List<Integer> quIds = Arrays.stream(temp1.getAnswerId().split(","))
                                    .map(Integer::parseInt)
                                    .collect(Collectors.toList());
                            if (quIds.contains(temp.getId())) {
                                temp.setCheckout(true);
                            } else {
                                temp.setCheckout(false);
                            }
                            break;
                        case 4:
                            temp.setContent(answerContent);
                            examQuDetailVO.setAnswerList(optionVOS);
                            break;
                        default:
                            break;
                    }
                }
                ;
            }

        }
        if (quById.getQuType() != 4) {
            examQuDetailVO.setAnswerList(optionVOS);
        }
        return Result.success("获取成功", examQuDetailVO);
    }

    @Override
    public Result<List<ExamQuCollectVO>> getCollect(Integer examId) {
        
        if (!isUserTakingExam(examId)) {
            return Result.failed("没有考试在进行");
        }
        List<ExamQuCollectVO> examQuCollectVOS = new ArrayList<>();
        
        LambdaQueryWrapper<ExamQuestion> examQuestionWrapper = new LambdaQueryWrapper<>();
        examQuestionWrapper.eq(ExamQuestion::getExamId, examId)
                .orderByAsc(ExamQuestion::getSort);
        List<ExamQuestion> examQuestions = examQuestionMapper.selectList(examQuestionWrapper);
        List<Integer> quIds = examQuestions.stream()
                .map(ExamQuestion::getQuestionId)
                .collect(Collectors.toList());
        
        List<Question> questions = questionMapper.selectBatchIds(quIds);
        for (Question temp : questions) {
            
            ExamQuCollectVO examQuCollectVO = new ExamQuCollectVO();
            
            examQuCollectVO.setTitle(temp.getContent());
            examQuCollectVO.setQuType(temp.getQuType());
            
            examQuCollectVO.setId(temp.getId());

            
            LambdaQueryWrapper<Option> optionWrapper = new LambdaQueryWrapper<>();
            optionWrapper.eq(Option::getQuId, temp.getId());
            List<Option> options = optionMapper.selectList(optionWrapper);
            if (temp.getQuType() == 4) {
                examQuCollectVO.setOption(null);
            } else {
                examQuCollectVO.setOption(options);
            }


            
            LambdaQueryWrapper<ExamQuAnswer> examQuAnswerWrapper = new LambdaQueryWrapper<>();
            examQuAnswerWrapper.eq(ExamQuAnswer::getUserId, SecurityUtil.getUserId())
                    .eq(ExamQuAnswer::getExamId, examId)
                    .eq(ExamQuAnswer::getQuestionId, temp.getId());
            ExamQuAnswer examQuAnswer = examQuAnswerMapper.selectOne(examQuAnswerWrapper);
            
            if (examQuAnswer == null) {
                examQuCollectVO.setMyOption(null);
                examQuCollectVOS.add(examQuCollectVO);
                continue;
            }
            switch (temp.getQuType()) {
                case 1:
                    
                    LambdaQueryWrapper<Option> optionLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
                    optionLambdaQueryWrapper1.eq(Option::getId, examQuAnswer.getAnswerId());
                    Option op1 = optionMapper.selectOne(optionLambdaQueryWrapper1);
                    examQuCollectVO.setMyOption(Integer.toString(op1.getSort()));
                    break;
                case 2:
                    
                    String answerId = examQuAnswer.getAnswerId();
                    List<Integer> opIds = Arrays.stream(answerId.split(","))
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                    
                    List<Integer> sorts = new ArrayList<>();
                    for (Integer opId : opIds) {
                        LambdaQueryWrapper<Option> optionLambdaQueryWrapper2 = new LambdaQueryWrapper<>();
                        optionLambdaQueryWrapper2.eq(Option::getId, opId);
                        Option option = optionMapper.selectOne(optionLambdaQueryWrapper2);
                        sorts.add(option.getSort());
                    }
                    
                    List<String> shortList = sorts.stream().map(String::valueOf).collect(Collectors.toList());
                    String myOption = String.join(",", shortList);
                    examQuCollectVO.setMyOption(myOption);
                    break;
                case 3:
                    
                    LambdaQueryWrapper<Option> optionLambdaQueryWrapper3 = new LambdaQueryWrapper<>();
                    optionLambdaQueryWrapper3.eq(Option::getId, examQuAnswer.getAnswerId());
                    Option op3 = optionMapper.selectOne(optionLambdaQueryWrapper3);
                    examQuCollectVO.setMyOption(Integer.toString(op3.getSort()));
                    break;
                case 4:
                    examQuCollectVO.setMyOption(examQuAnswer.getAnswerContent());
                    break;
                default:
                    break;
            }
            ;
            examQuCollectVOS.add(examQuCollectVO);
        }
        return Result.success("查询成功", examQuCollectVOS);
    }

    @Override
    public Result<ExamDetailVO> getDetail(Integer examId) {
        
        Exam exam = this.getById(examId);
        
        ExamDetailVO examDetailVO = examConverter.examToExamDetailVO(exam);
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getId, examDetailVO.getUserId());
        User user = userMapper.selectOne(userLambdaQueryWrapper);
        examDetailVO.setUsername(user.getUserName());
        return Result.success("查询成功", examDetailVO);
    }

    @Override
    public Result<Integer> addCheat(Integer examId) {
        LambdaQueryWrapper<UserExamsScore> userExamsScoreLambdaQuery = new LambdaQueryWrapper<>();
        userExamsScoreLambdaQuery.eq(UserExamsScore::getExamId, examId)
                .eq(UserExamsScore::getUserId, SecurityUtil.getUserId());
        UserExamsScore userExamsScore = userExamsScoreMapper.selectOne(userExamsScoreLambdaQuery);
        Exam exam = this.getById(examId);
        
        if (userExamsScore.getCount() >= exam.getMaxCount()) {
            this.handExam(examId);
            return Result.success("已超过最大切屏次数，已自动交卷", 1);
        }
        LambdaUpdateWrapper<UserExamsScore> userExamsScoreLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userExamsScoreLambdaUpdateWrapper.eq(UserExamsScore::getExamId, examId)
                .eq(UserExamsScore::getUserId, SecurityUtil.getUserId())
                .set(UserExamsScore::getCount, userExamsScore.getCount() + 1);
        int insert = userExamsScoreMapper.update(userExamsScoreLambdaUpdateWrapper);
        return Result.success("请勿切屏，最大切屏次数：" + exam.getMaxCount() + ",已切屏次数:" + (userExamsScore.getCount() + 1), 0);
    }

    @Override
    public Result<String> addAnswer(ExamQuAnswerAddForm examQuAnswerForm) {
        
        
        
        
        
        LambdaQueryWrapper<Question> QuWrapper = new LambdaQueryWrapper<>();
        QuWrapper.eq(Question::getId, examQuAnswerForm.getQuId());
        Question qu = questionMapper.selectOne(QuWrapper);
        Integer quType = qu.getQuType();
        
        LambdaQueryWrapper<ExamQuAnswer> examQuAnswerLambdaQueryWrapper = new LambdaQueryWrapper<>();
        examQuAnswerLambdaQueryWrapper.eq(ExamQuAnswer::getExamId, examQuAnswerForm.getExamId())
                .eq(ExamQuAnswer::getQuestionId, examQuAnswerForm.getQuId())
                .eq(ExamQuAnswer::getUserId, SecurityUtil.getUserId());
        List<ExamQuAnswer> existingAnswers = examQuAnswerMapper.selectList(examQuAnswerLambdaQueryWrapper);
        if (!existingAnswers.isEmpty()) {
            
            return updateAnswerIfExists(examQuAnswerForm, quType);
        } else {
            
            return insertNewAnswer(examQuAnswerForm, quType);
        }
    }

    @Override
    public Result<String> insertNewAnswer(ExamQuAnswerAddForm examQuAnswerForm, Integer quType) {
        
        ExamQuAnswer examQuAnswer = prepareExamQuAnswer(examQuAnswerForm, quType);
        switch (quType) {
            case 1:
                Option byId1 = optionService.getById(examQuAnswerForm.getAnswer());
                if (byId1.getIsRight() == 1) {
                    examQuAnswer.setIsRight(1);
                    examQuAnswerMapper.insert(examQuAnswer);
                    return Result.success("请求成功");
                } else {
                    examQuAnswer.setIsRight(0);
                    examQuAnswerMapper.insert(examQuAnswer);
                    return Result.success("请求成功");
                }
            case 2:
                
                LambdaQueryWrapper<Option> optionWrapper = new LambdaQueryWrapper<>();
                optionWrapper.eq(Option::getIsRight, 1)
                        .eq(Option::getQuId, examQuAnswerForm.getQuId());
                List<Option> examQuAnswers = optionMapper.selectList(optionWrapper);
                
                List<Integer> quIds = Arrays.stream(examQuAnswerForm.getAnswer().split(","))
                        .map(Integer::parseInt)
                        .collect(java.util.stream.Collectors.toList());
                
                boolean isRight2 = true;
                for (Option temp : examQuAnswers) {
                    if (!quIds.contains(temp.getId())) {
                        isRight2 = false;
                        break;
                    }
                }
                if (isRight2) {
                    examQuAnswer.setIsRight(1);
                } else {
                    examQuAnswer.setIsRight(0);
                }
                examQuAnswerMapper.insert(examQuAnswer);
                return Result.success("请求成功");
            case 3:
                Option byId3 = optionService.getById(examQuAnswerForm.getAnswer());
                if (byId3.getIsRight() == 1) {
                    examQuAnswer.setIsRight(1);
                    examQuAnswerMapper.insert(examQuAnswer);
                    return Result.success("请求成功");
                } else {
                    examQuAnswer.setIsRight(0);
                    examQuAnswerMapper.insert(examQuAnswer);
                    return Result.success("请求成功");
                }
            case 4:
                LambdaQueryWrapper<Option> optionLambdaQueryWrapper = new LambdaQueryWrapper<>();
                optionLambdaQueryWrapper.eq(Option::getQuId, examQuAnswerForm.getQuId());
                Option option = optionMapper.selectOne(optionLambdaQueryWrapper);
                if (option.getContent().equals(examQuAnswerForm.getAnswer())) {
                    examQuAnswer.setIsRight(1);
                } else {
                    examQuAnswer.setIsRight(0);
                }
                examQuAnswerMapper.insert(examQuAnswer);
                return Result.success("请求成功");
            default:
                return Result.failed("请求错误，请联系管理员解决");
        }
    }

    @Override
    public Result<String> updateAnswerIfExists(ExamQuAnswerAddForm examQuAnswerForm, Integer quType) {
        
        switch (quType) {
            case 1:
                Option byId = optionService.getById(examQuAnswerForm.getAnswer());
                if (byId == null) {
                    return Result.failed("数据库中不存在该试题，请联系管理员解决");
                }
                LambdaUpdateWrapper<ExamQuAnswer> updateWrapper1;
                if (byId.getIsRight() == 1) {
                    updateWrapper1 = new LambdaUpdateWrapper<>();
                    updateWrapper1.eq(ExamQuAnswer::getUserId, SecurityUtil.getUserId())
                            .eq(ExamQuAnswer::getExamId, examQuAnswerForm.getExamId())
                            .eq(ExamQuAnswer::getQuestionId, examQuAnswerForm.getQuId())
                            .set(ExamQuAnswer::getIsRight, 1)
                            .set(ExamQuAnswer::getAnswerId, examQuAnswerForm.getAnswer());
                } else {
                    updateWrapper1 = new LambdaUpdateWrapper<>();
                    updateWrapper1.eq(ExamQuAnswer::getUserId, SecurityUtil.getUserId())
                            .eq(ExamQuAnswer::getExamId, examQuAnswerForm.getExamId())
                            .eq(ExamQuAnswer::getQuestionId, examQuAnswerForm.getQuId())
                            .set(ExamQuAnswer::getIsRight, 0)
                            .set(ExamQuAnswer::getAnswerId, examQuAnswerForm.getAnswer());
                }
                examQuAnswerMapper.update(null, updateWrapper1);
                return Result.success("请求成功");
            case 2:
                
                LambdaQueryWrapper<Option> optionWrapper = new LambdaQueryWrapper<>();
                optionWrapper.eq(Option::getIsRight, 1)
                        .eq(Option::getQuId, examQuAnswerForm.getQuId());
                List<Option> examQuAnswers = optionMapper.selectList(optionWrapper);
                if (examQuAnswers.isEmpty()) {
                    return Result.failed("该题正确答案选项不存在");
                }
                
                List<Integer> quIds = Arrays.stream(examQuAnswerForm.getAnswer().split(","))
                        .map(Integer::parseInt)
                        .collect(java.util.stream.Collectors.toList());
                
                boolean isRight = true;
                for (Option temp : examQuAnswers) {
                    if (!quIds.contains(temp.getId())) {
                        isRight = false;
                        break;
                    }
                }
                LambdaUpdateWrapper<ExamQuAnswer> updateWrapper2 = new LambdaUpdateWrapper<>();
                updateWrapper2.eq(ExamQuAnswer::getUserId, SecurityUtil.getUserId())
                        .eq(ExamQuAnswer::getExamId, examQuAnswerForm.getExamId())
                        .eq(ExamQuAnswer::getQuestionId, examQuAnswerForm.getQuId())
                        .set(ExamQuAnswer::getAnswerId, examQuAnswerForm.getAnswer());
                if (isRight) {
                    updateWrapper2.set(ExamQuAnswer::getIsRight, 1);
                } else {
                    updateWrapper2.set(ExamQuAnswer::getIsRight, 0);
                }
                examQuAnswerMapper.update(null, updateWrapper2);
                return Result.success("请求成功");
            case 3:
                Option byId3 = optionService.getById(examQuAnswerForm.getAnswer());
                if (byId3 == null) {
                    return Result.failed("数据库中不存在该试题，请联系管理员解决");
                }
                LambdaUpdateWrapper<ExamQuAnswer> updateWrapper3;
                if (byId3.getIsRight() == 1) {
                    updateWrapper3 = new LambdaUpdateWrapper<>();
                    updateWrapper3.eq(ExamQuAnswer::getUserId, SecurityUtil.getUserId())
                            .eq(ExamQuAnswer::getExamId, examQuAnswerForm.getExamId())
                            .eq(ExamQuAnswer::getQuestionId, examQuAnswerForm.getQuId())
                            .set(ExamQuAnswer::getIsRight, 1)
                            .set(ExamQuAnswer::getAnswerId, examQuAnswerForm.getAnswer());
                } else {
                    updateWrapper3 = new LambdaUpdateWrapper<>();
                    updateWrapper3.eq(ExamQuAnswer::getUserId, SecurityUtil.getUserId())
                            .eq(ExamQuAnswer::getExamId, examQuAnswerForm.getExamId())
                            .eq(ExamQuAnswer::getQuestionId, examQuAnswerForm.getQuId())
                            .set(ExamQuAnswer::getIsRight, 0)
                            .set(ExamQuAnswer::getAnswerId, examQuAnswerForm.getAnswer());
                }
                examQuAnswerMapper.update(null, updateWrapper3);
                return Result.success("请求成功");
            case 4:
                LambdaUpdateWrapper<ExamQuAnswer> updateWrapper4 = new LambdaUpdateWrapper<>();
                updateWrapper4.eq(ExamQuAnswer::getUserId, SecurityUtil.getUserId())
                        .eq(ExamQuAnswer::getExamId, examQuAnswerForm.getExamId())
                        .eq(ExamQuAnswer::getQuestionId, examQuAnswerForm.getQuId())
                        .set(ExamQuAnswer::getAnswerContent, examQuAnswerForm.getAnswer());
                examQuAnswerMapper.update(null, updateWrapper4);
                return Result.success("请求成功");
            default:
                return Result.failed("请求错误，请联系管理员解决");
        }
    }

    @Override
    public ExamQuAnswer prepareExamQuAnswer(ExamQuAnswerAddForm form, Integer quType) {
        
        ExamQuAnswer examQuAnswer = examQuAnswerConverter.formToEntity(form);
        if (quType == 4) {
            examQuAnswer.setAnswerContent(form.getAnswer());
        } else {
            examQuAnswer.setAnswerId(form.getAnswer());
        }
        examQuAnswer.setUserId(SecurityUtil.getUserId());
        examQuAnswer.setQuestionType(quType);
        return examQuAnswer;
    }

    @Override
    public boolean isUserTakingExam(Integer examId) {
        
        LambdaQueryWrapper<UserExamsScore> userExamsScoreLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userExamsScoreLambdaQueryWrapper.eq(UserExamsScore::getUserId, SecurityUtil.getUserId())
                .eq(UserExamsScore::getExamId, examId)
                .eq(UserExamsScore::getState, 0);
        List<UserExamsScore> userExamsScores = userExamsScoreMapper.selectList(userExamsScoreLambdaQueryWrapper);
        if (userExamsScores.size() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public Result<List<ExamRecordDetailVO>> details(Integer examId) {
        
        List<ExamRecordDetailVO> examRecordDetailVOS = new ArrayList<>();
        
        LambdaQueryWrapper<ExamQuestion> examQuestionWrapper = new LambdaQueryWrapper<>();
        examQuestionWrapper.eq(ExamQuestion::getExamId, examId)
                .orderByAsc(ExamQuestion::getSort);
        List<ExamQuestion> examQuestions = examQuestionMapper.selectList(examQuestionWrapper);
        List<Integer> quIds = examQuestions.stream()
                .map(ExamQuestion::getQuestionId)
                .collect(Collectors.toList());
        
        List<Question> questions = questionMapper.selectBatchIds(quIds);
        for (Question temp : questions) {
            
            ExamRecordDetailVO examRecordDetailVO = new ExamRecordDetailVO();
            
            examRecordDetailVO.setImage(temp.getImage());
            examRecordDetailVO.setTitle(temp.getContent());
            examRecordDetailVO.setQuType(temp.getQuType());
            
            examRecordDetailVO.setAnalyse(temp.getAnalysis());
            
            LambdaQueryWrapper<Option> optionWrapper = new LambdaQueryWrapper<>();
            optionWrapper.eq(Option::getQuId, temp.getId());
            List<Option> options = optionMapper.selectList(optionWrapper);
            if (temp.getQuType() == 4) {
                examRecordDetailVO.setOption(null);
            } else {
                examRecordDetailVO.setOption(options);
            }

            
            LambdaQueryWrapper<Question> QuWrapper = new LambdaQueryWrapper<>();
            QuWrapper.eq(Question::getId, temp.getId());
            Question qu = questionMapper.selectOne(QuWrapper);
            Integer quType = qu.getQuType();
            
            LambdaQueryWrapper<Option> opWrapper = new LambdaQueryWrapper<>();
            opWrapper.eq(Option::getQuId, temp.getId());
            List<Option> opList = optionMapper.selectList(opWrapper);

            if (temp.getQuType() == 4 && opList.size() > 0) {
                examRecordDetailVO.setRightOption(opList.get(0).getContent());
            } else {
                String current = "";
                ArrayList<Integer> strings = new ArrayList<>();
                for (Option temp1 : options) {
                    if (temp1.getIsRight() == 1) {
                        strings.add(temp1.getSort());
                    }
                }
                List<String> stringList = strings.stream().map(String::valueOf).collect(Collectors.toList());
                String result = String.join(",", stringList);

                examRecordDetailVO.setRightOption(result);
            }
            examRecordDetailVOS.add(examRecordDetailVO);
        }
        if (examRecordDetailVOS == null) {
            throw new ServiceRuntimeException("查询考试的信息失败");
        }
        return Result.success("查询考试的信息成功", examRecordDetailVOS);

    }

    @Override
    public Result<IPage<ExamGradeListVO>> getGradeExamList(Integer pageNum, Integer pageSize, String title, Boolean isASC) {
        
        IPage<ExamGradeListVO> examPage = new Page<>(pageNum, pageSize);
        
        Integer userId = SecurityUtil.getUserId();
        
        String role = SecurityUtil.getRole();
        
        if ("role_student".equals(role)) {
            examGradeMapper.selectClassExam(examPage, userId, title, isASC);
        } else if ("role_admin".equals(role)) {
            examGradeMapper.selectAdminClassExam(examPage, userId, title, isASC);
        }
        
        return Result.success("查询成功", examPage);
    }


    @Override
    @Transactional
    public Result<ExamQuDetailVO> handExam(Integer examId) {
        
        if (!isUserTakingExam(examId)) {
            return Result.failed("没有考试在进行");
        }
        
        LocalDateTime nowTime = LocalDateTime.now();
        
        Exam examOne = this.getById(examId);
        if (examOne == null) {
            return Result.failed("考试不存在: " + examId);
        }

        
        LambdaQueryWrapper<UserExamsScore> userScoreQuery = new LambdaQueryWrapper<>();
        userScoreQuery.eq(UserExamsScore::getUserId, SecurityUtil.getUserId())
                .eq(UserExamsScore::getExamId, examId)
                .eq(UserExamsScore::getState, 0); 
        UserExamsScore userExamsScore1 = userExamsScoreMapper.selectOne(userScoreQuery);

        
        if (userExamsScore1 == null || userExamsScore1.getCreateTime() == null) {
            
            return Result.failed("交卷失败，无法确定考试开始时间或状态异常。");
        }

        
        LocalDateTime userStartTime = userExamsScore1.getCreateTime();
        LocalDateTime userEndTime = userStartTime.plusMinutes(examOne.getExamDuration());

        
        if (nowTime.isAfter(userEndTime)) {
            return Result.failed("提交失败，已过交卷时间");
        }

        
        UserExamsScore userExamsScoreToUpdate = new UserExamsScore(); 
        userExamsScoreToUpdate.setUserScore(0); 
        userExamsScoreToUpdate.setState(1); 
        userExamsScoreToUpdate.setLimitTime(nowTime); 

        
        List<ExamQuestion> unansweredSaqQuestions = examQuestionMapper.getUnansweredSaqQuestions(examId, SecurityUtil.getUserId());
        if (unansweredSaqQuestions != null && !unansweredSaqQuestions.isEmpty()) {
            for (ExamQuestion question : unansweredSaqQuestions) {
                ExamQuAnswer examQuAnswer = new ExamQuAnswer();
                examQuAnswer.setExamId(examId);
                examQuAnswer.setUserId(SecurityUtil.getUserId());
                examQuAnswer.setQuestionId(question.getQuestionId());
                examQuAnswer.setQuestionType(4); 
                examQuAnswer.setAnswerContent(""); 
                examQuAnswer.setIsRight(0); 
                examQuAnswerMapper.insert(examQuAnswer);
            }
        }

        
        LambdaQueryWrapper<ExamQuAnswer> examQuAnswerLambdaQuery = new LambdaQueryWrapper<>();
        examQuAnswerLambdaQuery.eq(ExamQuAnswer::getUserId, SecurityUtil.getUserId())
                .eq(ExamQuAnswer::getExamId, examId);
        List<ExamQuAnswer> examQuAnswer = examQuAnswerMapper.selectList(examQuAnswerLambdaQuery);

        
        List<UserBook> userBookArrayList = new ArrayList<>();
        int calculatedScore = 0; 
        for (ExamQuAnswer temp : examQuAnswer) {
            
            if (temp.getIsRight() != null && temp.getIsRight() == 1) {
                Integer questionType = temp.getQuestionType();
                if (questionType != null) {
                    if (questionType == 1 && examOne.getRadioScore() != null) {
                        calculatedScore += examOne.getRadioScore();
                    } else if (questionType == 2 && examOne.getMultiScore() != null) {
                        calculatedScore += examOne.getMultiScore();
                    } else if (questionType == 3 && examOne.getJudgeScore() != null) {
                        calculatedScore += examOne.getJudgeScore();
                    }
                }
            } else if (temp.getIsRight() != null && temp.getIsRight() == 0) { 
                UserBook userBook = new UserBook();
                userBook.setExamId(examId);
                userBook.setUserId(SecurityUtil.getUserId());
                userBook.setQuId(temp.getQuestionId());
                userBook.setCreateTime(nowTime); 
                userBookArrayList.add(userBook);
            }
        }

        
        if (!userBookArrayList.isEmpty()) {
            userBookMapper.addUserBookList(userBookArrayList);
        }

        
        long secondsDifference = Duration.between(userStartTime, nowTime).getSeconds();
        int userTime = (int) secondsDifference; 

        
        int whetherMark = -1; 
        if (examOne.getSaqCount() != null && examOne.getSaqCount() > 0) {
            whetherMark = 0; 
        }

        LambdaUpdateWrapper<UserExamsScore> userExamsScoreLambdaUpdate = new LambdaUpdateWrapper<>();
        userExamsScoreLambdaUpdate.eq(UserExamsScore::getUserId, SecurityUtil.getUserId())
                .eq(UserExamsScore::getExamId, examId)
                .eq(UserExamsScore::getState, 0); 

        userExamsScoreLambdaUpdate.set(UserExamsScore::getUserScore, calculatedScore); 
        userExamsScoreLambdaUpdate.set(UserExamsScore::getState, 1); 
        userExamsScoreLambdaUpdate.set(UserExamsScore::getLimitTime, nowTime); 
        userExamsScoreLambdaUpdate.set(UserExamsScore::getUserTime, userTime); 
        userExamsScoreLambdaUpdate.set(UserExamsScore::getWhetherMark, whetherMark); 

        
        int updateRows = userExamsScoreMapper.update(null, userExamsScoreLambdaUpdate); 

        
        if (updateRows == 0) {
            
            UserExamsScore latestScore = userExamsScoreMapper.selectOne(userScoreQuery.last("limit 1")); 
            if (latestScore != null && latestScore.getState() != 0) {
                return Result.failed("交卷失败，考试已被提交或状态异常。");
            } else {
                return Result.failed("交卷失败，更新记录时发生未知错误。");
            }
        }

        
        if (whetherMark == 0) {
            autoScoringService.autoScoringExam(examId, SecurityUtil.getUserId());
            return Result.success("提交成功，待老师阅卷");
        }

        
        if (whetherMark == -1 && examOne.getCertificateId() != null && examOne.getPassedScore() != null && calculatedScore >= examOne.getPassedScore()) {
            CertificateUser certificateUser = new CertificateUser();
            certificateUser.setCertificateId(examOne.getCertificateId());
            certificateUser.setUserId(SecurityUtil.getUserId());
            certificateUser.setExamId(examId);
            certificateUser.setCode(ClassTokenGenerator.generateClassToken(18));
            certificateUserMapper.insert(certificateUser);
        }
        return Result.success("交卷成功");
    }

    @Override
    public Result<String> startExam(Integer examId) {
        
        if (isUserTakingExam(examId)) {
            return Result.failed("已经有考试正在进行");
        }
        LambdaQueryWrapper<UserExamsScore> userExamsScoreLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userExamsScoreLambdaQueryWrapper.eq(UserExamsScore::getUserId, SecurityUtil.getUserId())
                .eq(UserExamsScore::getExamId, examId);
        List<UserExamsScore> userExamsScores = userExamsScoreMapper.selectList(userExamsScoreLambdaQueryWrapper);
        if (!userExamsScores.isEmpty()) {
            return Result.failed("这场考试已考不能第二次考试");
        }
        Exam exam = this.getById(examId);
        
        UserExamsScore userExamsScore = new UserExamsScore();
        userExamsScore.setExamId(examId);
        userExamsScore.setTotalTime(exam.getExamDuration());
        userExamsScore.setState(0);
        int rows = userExamsScoreMapper.insert(userExamsScore);
        if (rows == 0) {
            return Result.failed("访问失败");
        }
        return Result.success("已开始考试");
    }

    private LocalDateTime getUserStarExamTime(Integer examId, Integer userId) {
        LambdaQueryWrapper<UserExamsScore> userExamsScoreLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userExamsScoreLambdaQueryWrapper.eq(UserExamsScore::getUserId, userId)
                .eq(UserExamsScore::getExamId, examId)
                .eq(UserExamsScore::getState, 0);
        UserExamsScore userExamsScore = userExamsScoreMapper.selectOne(userExamsScoreLambdaQueryWrapper);
        LocalDateTime createTime = userExamsScore.getCreateTime();
        return createTime;
    }
}
