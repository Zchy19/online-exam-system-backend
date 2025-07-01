package cn.org.alan.exam.service;

import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.model.entity.Question;
import cn.org.alan.exam.model.form.question.QuestionFrom;
import cn.org.alan.exam.model.vo.question.QuestionVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;


public interface IQuestionService extends IService<Question> {

    
    Result<String> addSingleQuestion(QuestionFrom questionFrom);

    
    Result<String> deleteBatchByIds(String ids);


    
    Result<IPage<QuestionVO>> pagingQuestion(Integer pageNum, Integer pageSize, String content, Integer type, Integer repoId);

    
    Result<QuestionVO> querySingle(Integer id);

    
    Result<String> updateQuestion(QuestionFrom questionFrom);

    
    Result<String> importQuestion(Integer id, MultipartFile file);

}
