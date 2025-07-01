package cn.org.alan.exam.service.impl;

import cn.org.alan.exam.common.exception.ServiceRuntimeException;
import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.converter.ExerciseConverter;
import cn.org.alan.exam.converter.RecordConverter;
import cn.org.alan.exam.mapper.*;
import cn.org.alan.exam.model.entity.*;
import cn.org.alan.exam.model.form.exercise.ExerciseFillAnswerFrom;
import cn.org.alan.exam.model.vo.question.QuestionVO;
import cn.org.alan.exam.model.vo.exercise.AnswerInfoVO;
import cn.org.alan.exam.model.vo.exercise.QuestionSheetVO;
import cn.org.alan.exam.model.vo.record.ExamRecordDetailVO;
import cn.org.alan.exam.model.vo.record.ExamRecordVO;
import cn.org.alan.exam.model.vo.record.ExerciseRecordDetailVO;
import cn.org.alan.exam.model.vo.record.ExerciseRecordVO;
import cn.org.alan.exam.service.IExerciseRecordService;
import cn.org.alan.exam.service.IOptionService;
import cn.org.alan.exam.utils.SecurityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ExerciseRecordServiceImpl extends ServiceImpl<ExerciseRecordMapper, ExerciseRecord>
        implements IExerciseRecordService {

    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private ExamMapper examMapper;
    @Resource
    private RecordConverter recordConverter;
    @Resource
    private ExamQuestionMapper examQuestionMapper;
    @Resource
    private OptionMapper optionMapper;
    @Resource
    private ExamQuAnswerMapper examQuAnswerMapper;
    @Resource
    private IOptionService optionService;
    @Resource
    private UserExerciseRecordMapper userExerciseRecordMapper;
    @Resource
    private RepoMapper repoMapper;
    @Resource
    private ExerciseConverter exerciseConverter;
    @Resource
    private ExerciseRecordMapper exerciseRecordMapper;


    @Override
    public Result<List<QuestionSheetVO>> getQuestionSheet(Integer repoId, Integer quType) {
        List<QuestionSheetVO> list = questionMapper.selectQuestionSheet(repoId, quType, SecurityUtil.getUserId());
        return Result.success("获取获取试题答题卡列表成功", list);
    }

    @Override
    public Result<IPage<ExamRecordVO>> getExamRecordPage(Integer pageNum, Integer pageSize, String examName, Boolean isASC) {
        
        Page<ExamRecordVO> examPage = new Page<>(pageNum, pageSize);
        
        
        Integer userId = SecurityUtil.getUserId();
        Integer roleCode = SecurityUtil.getRoleCode();
        
        
        if (roleCode==3) {
            
            examPage = examMapper.getAllExamRecordPage(examPage, examName, isASC);
        } else if (roleCode==2) {
            
            examPage = examMapper.getTeacherExamRecordPage(examPage, userId, examName, isASC);
        } else {
            
            examPage = examMapper.getExamRecordPage(examPage, userId, examName, isASC);
        }
        
        return Result.success("分页查询已考试试卷成功", examPage);
    }

    @Override
    public Result<List<ExamRecordDetailVO>> getExamRecordDetail(Integer examId, Integer userId) {
        if(userId==null){
            userId =SecurityUtil.getUserId();
        }
        
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
            
            LambdaQueryWrapper<ExamQuAnswer> examQuAnswerWrapper = new LambdaQueryWrapper<>();
            examQuAnswerWrapper.eq(ExamQuAnswer::getUserId, userId)
                    .eq(ExamQuAnswer::getExamId, examId)
                    .eq(ExamQuAnswer::getQuestionId, temp.getId());
            ExamQuAnswer examQuAnswer = examQuAnswerMapper.selectOne(examQuAnswerWrapper);
            
            if (examQuAnswer == null) {
                examRecordDetailVO.setMyOption(null);
                examRecordDetailVO.setIsRight(-1);
                examRecordDetailVOS.add(examRecordDetailVO);
                continue;
            }
            switch (quType) {
                case 1:
                    
                    LambdaQueryWrapper<Option> optionLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
                    optionLambdaQueryWrapper1.eq(Option::getId, examQuAnswer.getAnswerId());
                    Option op1 = optionMapper.selectOne(optionLambdaQueryWrapper1);
                    examRecordDetailVO.setMyOption(Integer.toString(op1.getSort()));
                    
                    Option byId1 = optionService.getById(examQuAnswer.getAnswerId());
                    if (byId1.getIsRight() == 1) {
                        examRecordDetailVO.setIsRight(1);
                    } else {
                        examRecordDetailVO.setIsRight(0);
                    }
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
                    examRecordDetailVO.setMyOption(myOption);
                    
                    LambdaQueryWrapper<Option> optionWrapper1 = new LambdaQueryWrapper<>();
                    optionWrapper1.eq(Option::getIsRight, 1)
                            .eq(Option::getQuId, temp.getId());
                    List<Option> examQuAnswers = optionMapper.selectList(optionWrapper1);
                    
                    examRecordDetailVO.setIsRight(1);
                    for (Option temp1 : examQuAnswers) {
                        boolean contains = opIds.contains(temp1.getId());
                        if (!contains) {
                            
                            examRecordDetailVO.setIsRight(0);
                            break;
                        }
                    }
                    break;
                case 3:
                    
                    LambdaQueryWrapper<Option> optionLambdaQueryWrapper3 = new LambdaQueryWrapper<>();
                    optionLambdaQueryWrapper3.eq(Option::getId, examQuAnswer.getAnswerId());
                    Option op3 = optionMapper.selectOne(optionLambdaQueryWrapper3);
                    examRecordDetailVO.setMyOption(Integer.toString(op3.getSort()));
                    
                    Option byId3 = optionService.getById(examQuAnswer.getAnswerId());
                    if (byId3.getIsRight() == 1) {
                        examRecordDetailVO.setIsRight(1);
                    } else {
                        examRecordDetailVO.setIsRight(0);
                    }
                    break;
                case 4:
                    examRecordDetailVO.setMyOption(examQuAnswer.getAnswerContent());
                    examRecordDetailVO.setIsRight(-1);
                    break;
                default:
                    break;
            }
            examRecordDetailVOS.add(examRecordDetailVO);

        }
        if (examRecordDetailVOS==null){
            throw new ServiceRuntimeException("查询考试的信息失败");
        }

        return Result.success("查询考试的信息成功", examRecordDetailVOS);
    }

    @Override
    public Result<IPage<ExerciseRecordVO>> getExerciseRecordPage(Integer pageNum, Integer pageSize ,String repoName) {
        
        Page<Repo> repoPage = new Page<>(pageNum, pageSize);
        
        Integer userId = SecurityUtil.getUserId();
        Page<Repo> exercisePageResult = repoMapper.selectUserExerciseRecord(repoPage,userId,repoName);
        
        Page<ExerciseRecordVO> exerciseRecordVOPage = recordConverter.pageRepoEntityToVo(exercisePageResult);
        return Result.success("查询成功", exerciseRecordVOPage);
    }

    @Override
    public Result<List<ExerciseRecordDetailVO>> getExerciseRecordDetail(Integer exerciseId) {
        
        List<ExerciseRecordDetailVO> exerciseRecordDetailVOS = new ArrayList<>();
        
        LambdaQueryWrapper<Question> questionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        questionLambdaQueryWrapper.eq(Question::getRepoId, exerciseId);
        List<Question> questions1 = questionMapper.selectList(questionLambdaQueryWrapper);
        for (Question temp : questions1) {
            ExerciseRecordDetailVO exerciseRecordDetailVO = new ExerciseRecordDetailVO();
            exerciseRecordDetailVO.setImage(temp.getImage());
            exerciseRecordDetailVO.setTitle(temp.getContent());
            exerciseRecordDetailVO.setAnalyse(temp.getAnalysis());
            exerciseRecordDetailVO.setQuType(temp.getQuType());
            
            LambdaQueryWrapper<Option> optionWrapper = new LambdaQueryWrapper<>();
            optionWrapper.eq(Option::getQuId, temp.getId());
            List<Option> options = optionMapper.selectList(optionWrapper);
            if (temp.getQuType() == 4) {
                exerciseRecordDetailVO.setOption(null);
            } else {
                exerciseRecordDetailVO.setOption(options);
            }

            if (temp.getQuType() == 4 && options.size() > 0) {
                exerciseRecordDetailVO.setRightOption(options.get(0).getContent());
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

                exerciseRecordDetailVO.setRightOption(result);
            }
            LambdaQueryWrapper<ExerciseRecord> exerciseRecordLambdaQueryWrapper = new LambdaQueryWrapper<>();
            exerciseRecordLambdaQueryWrapper.eq(ExerciseRecord::getUserId, SecurityUtil.getUserId())
                    .eq(ExerciseRecord::getRepoId, exerciseId)
                    .eq(ExerciseRecord::getQuestionId, temp.getId());
            ExerciseRecord exerciseRecord = exerciseRecordMapper.selectOne(exerciseRecordLambdaQueryWrapper);

            
            if (exerciseRecord == null) {
                exerciseRecordDetailVO.setMyOption(null);
                exerciseRecordDetailVO.setIsRight(-1);
                exerciseRecordDetailVOS.add(exerciseRecordDetailVO);
                continue;
            }
            switch (temp.getQuType()) {
                case 1:
                    
                    LambdaQueryWrapper<Option> optionLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
                    optionLambdaQueryWrapper1.eq(Option::getId, exerciseRecord.getAnswer());
                    Option op1 = optionMapper.selectOne(optionLambdaQueryWrapper1);
                    exerciseRecordDetailVO.setMyOption(Integer.toString(op1.getSort()));
                    
                    Option byId1 = optionService.getById(exerciseRecord.getAnswer());
                    if (byId1.getIsRight() == 1) {
                        exerciseRecordDetailVO.setIsRight(1);
                    } else {
                        exerciseRecordDetailVO.setIsRight(0);
                    }
                    break;
                case 2:
                    
                    String answerId = exerciseRecord.getAnswer();
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
                    exerciseRecordDetailVO.setMyOption(myOption);
                    
                    LambdaQueryWrapper<Option> optionWrapper1 = new LambdaQueryWrapper<>();
                    optionWrapper1.eq(Option::getIsRight, 1)
                            .eq(Option::getQuId, temp.getId());
                    List<Option> examQuAnswers = optionMapper.selectList(optionWrapper1);
                    
                    exerciseRecordDetailVO.setIsRight(1);
                    for (Option temp1 : examQuAnswers) {
                        boolean contains = opIds.contains(temp1.getId());
                        if (!contains) {
                            
                            exerciseRecordDetailVO.setIsRight(0);
                            break;
                        }
                    }
                    break;
                case 3:
                    
                    LambdaQueryWrapper<Option> optionLambdaQueryWrapper3 = new LambdaQueryWrapper<>();
                    optionLambdaQueryWrapper3.eq(Option::getId, exerciseRecord.getAnswer());
                    Option op3 = optionMapper.selectOne(optionLambdaQueryWrapper3);
                    exerciseRecordDetailVO.setMyOption(Integer.toString(op3.getSort()));
                    
                    Option byId3 = optionService.getById(exerciseRecord.getAnswer());
                    if (byId3.getIsRight() == 1) {
                        exerciseRecordDetailVO.setIsRight(1);
                    } else {
                        exerciseRecordDetailVO.setIsRight(0);
                    }
                    break;
                case 4:
                    exerciseRecordDetailVO.setMyOption(null);
                    exerciseRecordDetailVO.setIsRight(-1);
                    break;
                default:
                    break;
            }
            exerciseRecordDetailVOS.add(exerciseRecordDetailVO);
        }
        return Result.success("查询刷题详情成功", exerciseRecordDetailVOS);
    }

    @Override
    @Transactional
    public Result<QuestionVO> fillAnswer(ExerciseFillAnswerFrom exerciseFillAnswerFrom) {
        ExerciseRecord exerciseRecord = exerciseConverter.fromToEntity(exerciseFillAnswerFrom);
        
        boolean flag = true;
        exerciseRecord.setIsRight(1);

        
        if (exerciseFillAnswerFrom.getQuType() != 4) {
            List<Integer> options = Arrays.stream(exerciseRecord.getAnswer().split(","))
                    .map(Integer::parseInt).collect(java.util.stream.Collectors.toList());
            List<Integer> rightOptions = new ArrayList<>();
            optionMapper.selectAllByQuestionId(exerciseRecord.getQuestionId()).forEach(option -> {
                if (option.getIsRight() == 1) {
                    rightOptions.add(option.getId());
                }
            });
            if (options.size() != rightOptions.size()) {
                flag = false;
            } else {
                for (Integer option : options) {
                    if (!rightOptions.contains(option)) {
                        flag = false;
                        exerciseRecord.setIsRight(0);
                        break;
                    }
                }
            }
        }
        if (flag) {
            exerciseRecord.setIsRight(1);
        } else {
            exerciseRecord.setIsRight(0);
        }
        
        LambdaQueryWrapper<ExerciseRecord> exerciseRecordLambdaQueryWrapper = new LambdaQueryWrapper<ExerciseRecord>()
                .eq(ExerciseRecord::getUserId, SecurityUtil.getUserId())
                .eq(ExerciseRecord::getRepoId, exerciseRecord.getRepoId())
                .eq(ExerciseRecord::getQuestionId, exerciseRecord.getQuestionId());
        ExerciseRecord databaseExerciseRecord = exerciseRecordMapper.selectOne(exerciseRecordLambdaQueryWrapper);
        boolean exercised = !Optional.ofNullable(databaseExerciseRecord).isPresent();
        if (exercised) {
            
            exerciseRecordMapper.insert(exerciseRecord);
            
            LambdaQueryWrapper<UserExerciseRecord> exerciseRecordWrapper = new LambdaQueryWrapper<UserExerciseRecord>()
                    .eq(UserExerciseRecord::getUserId, SecurityUtil.getUserId())
                    .eq(UserExerciseRecord::getRepoId, exerciseRecord.getRepoId());
            UserExerciseRecord userExerciseRecord = userExerciseRecordMapper.selectOne(exerciseRecordWrapper);

            if (!Optional.ofNullable(userExerciseRecord).isPresent()) {
                
                LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<Question>()
                        .eq(Question::getRepoId, exerciseRecord.getRepoId());
                int totalCount = questionMapper.selectCount(questionWrapper).intValue();
                UserExerciseRecord insertUserExerciseRecord = new UserExerciseRecord();
                insertUserExerciseRecord.setExerciseCount(1);
                insertUserExerciseRecord.setRepoId(exerciseRecord.getRepoId());
                insertUserExerciseRecord.setTotalCount(totalCount);
                userExerciseRecordMapper.insert(insertUserExerciseRecord);
            } else {
                
                LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<Question>()
                        .eq(Question::getId, exerciseRecord.getRepoId());

                
                UserExerciseRecord updateUserExerciseRecord = new UserExerciseRecord();
                updateUserExerciseRecord.setTotalCount(questionMapper.selectCount(wrapper).intValue());
                updateUserExerciseRecord.setId(userExerciseRecord.getId());
                updateUserExerciseRecord.setExerciseCount(userExerciseRecord.getExerciseCount() + 1);
                userExerciseRecordMapper.updateById(updateUserExerciseRecord);
            }
        } else {
            
            exerciseRecord.setId(databaseExerciseRecord.getId());
            exerciseRecordMapper.updateById(exerciseRecord);
        }

        
        QuestionVO questionVO = questionMapper.selectSingle(exerciseRecord.getQuestionId());

        
        
        if (exerciseRecord.getQuestionType() == 4) {
            return Result.success(null, questionVO);
        }

        return flag ? Result.success("回答正确", questionVO) : Result.success("回答错误", questionVO);
    }

    @Override
    public Result<QuestionVO> getSingle(Integer id) {
        QuestionVO questionVO = questionMapper.selectDetail(id);
        return Result.success("查询单题成功", questionVO);
    }

    @Override
    public Result<AnswerInfoVO> getAnswerInfo(Integer repoId, Integer quId) {
        QuestionVO questionVO = questionMapper.selectSingle(quId);
        AnswerInfoVO answerInfoVO = exerciseConverter.quVOToAnswerInfoVO(questionVO);
        LambdaQueryWrapper<ExerciseRecord> exerciseRecordLambdaQueryWrapper = new LambdaQueryWrapper<ExerciseRecord>()
                .eq(ExerciseRecord::getRepoId, repoId)
                .eq(ExerciseRecord::getQuestionId, quId)
                .eq(ExerciseRecord::getUserId, SecurityUtil.getUserId());
        ExerciseRecord exerciseRecord = exerciseRecordMapper.selectOne(exerciseRecordLambdaQueryWrapper);
        answerInfoVO.setAnswerContent(exerciseRecord.getAnswer());
        return exerciseRecord.getIsRight() == 1 ?
                Result.success("回答正确", answerInfoVO) : Result.success("回答错误", answerInfoVO);

    }
}