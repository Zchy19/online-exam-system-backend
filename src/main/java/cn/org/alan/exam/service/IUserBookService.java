package cn.org.alan.exam.service;

import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.model.entity.UserBook;
import cn.org.alan.exam.model.form.userbook.ReUserBookForm;
import cn.org.alan.exam.model.vo.userbook.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface IUserBookService extends IService<UserBook> {
    
    Result<IPage<UserPageBookVO>> getPage(Integer pageNum, Integer pageSize, String examName);

    
    Result<List<ReUserExamBookVO>> getReUserExamBook(Integer examId);

    
    Result<BookOneQuVO> getBookOne(Integer quId);

    
    Result<AddBookAnswerVO> addBookAnswer(ReUserBookForm reUserBookForm);
}
