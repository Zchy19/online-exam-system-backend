package cn.org.alan.exam.service;

import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.model.entity.Exam;
import cn.org.alan.exam.model.entity.ExamQuAnswer;
import cn.org.alan.exam.model.form.exam.ExamAddForm;
import cn.org.alan.exam.model.form.exam.ExamUpdateForm;
import cn.org.alan.exam.model.form.exam_qu_answer.ExamQuAnswerAddForm;
import cn.org.alan.exam.model.vo.exam.*;
import cn.org.alan.exam.model.vo.record.ExamRecordDetailVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface IExamService extends IService<Exam> {
    
    Result<String> createExam(ExamAddForm examAddForm);

    
    Result<String> updateExam(ExamUpdateForm examUpdateForm, Integer examId);

    
    Result<String> deleteExam(String ids);

    
    Result<IPage<ExamVO>> getPagingExam(Integer pageNum, Integer pageSize, String title);

    
    Result<ExamQuestionListVO> getQuestionList(Integer examId);

    
    Result<ExamQuDetailVO> getQuestionSingle(Integer examId, Integer questionId);

    
    Result<List<ExamQuCollectVO>> getCollect(Integer examId);

    
    Result<ExamDetailVO> getDetail(Integer examId);

    
    Result<Integer> addCheat(Integer examId);

    
    Result<String> addAnswer(ExamQuAnswerAddForm examQuAnswerForm);

    
    Result<IPage<ExamGradeListVO>> getGradeExamList(Integer pageNum, Integer pageSize, String title, Boolean isASC);

    
    Result<ExamQuDetailVO> handExam(Integer examId);

    
    Result<String> startExam(Integer examId);

    
    Result<String> insertNewAnswer(ExamQuAnswerAddForm examQuAnswerForm, Integer quType);

    
    Result<String> updateAnswerIfExists(ExamQuAnswerAddForm examQuAnswerForm, Integer quType);

    
    ExamQuAnswer prepareExamQuAnswer(ExamQuAnswerAddForm form, Integer quType);

    
    boolean isUserTakingExam(Integer examId);

    
    Result<List<ExamRecordDetailVO>> details(Integer examId);
}
