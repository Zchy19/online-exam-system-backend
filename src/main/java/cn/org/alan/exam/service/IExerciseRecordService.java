package cn.org.alan.exam.service;

import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.model.entity.ExerciseRecord;
import cn.org.alan.exam.model.form.exercise.ExerciseFillAnswerFrom;
import cn.org.alan.exam.model.vo.question.QuestionVO;
import cn.org.alan.exam.model.vo.exercise.AnswerInfoVO;
import cn.org.alan.exam.model.vo.exercise.QuestionSheetVO;
import cn.org.alan.exam.model.vo.record.ExamRecordDetailVO;
import cn.org.alan.exam.model.vo.record.ExamRecordVO;
import cn.org.alan.exam.model.vo.record.ExerciseRecordDetailVO;
import cn.org.alan.exam.model.vo.record.ExerciseRecordVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface IExerciseRecordService extends IService<ExerciseRecord> {


    
    Result<List<QuestionSheetVO>> getQuestionSheet(Integer repoId, Integer quType);

    
    Result<IPage<ExamRecordVO>> getExamRecordPage(Integer pageNum, Integer pageSize, String examName, Boolean isASC);

    
    Result<List<ExamRecordDetailVO>> getExamRecordDetail(Integer examId, Integer userId);


    
    Result<IPage<ExerciseRecordVO>> getExerciseRecordPage(Integer pageNum, Integer pageSize, String repoName);

    
    Result<List<ExerciseRecordDetailVO>> getExerciseRecordDetail(Integer exerciseId);

    
    Result<QuestionVO> fillAnswer(ExerciseFillAnswerFrom exerciseFillAnswerFrom);

    
    Result<QuestionVO> getSingle(Integer id);

    
    Result<AnswerInfoVO> getAnswerInfo(Integer repoId, Integer quId);
}
