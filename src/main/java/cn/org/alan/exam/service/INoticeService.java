package cn.org.alan.exam.service;

import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.model.entity.Notice;
import cn.org.alan.exam.model.form.notice.NoticeForm;
import cn.org.alan.exam.model.vo.notice.NoticeVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;


public interface INoticeService extends IService<Notice> {

    
    Result<String> addNotice(NoticeForm noticeForm);

    
    Result<String> deleteNotice(String ids);

    
    Result<String> updateNotice(Integer id, NoticeForm noticeForm);

    
    Result<IPage<NoticeVO>> getNotice(Integer pageNum, Integer pageSize, String title);

    
    Result<IPage<NoticeVO>> getNewNotice(Integer pageNum, Integer pageSize);
}
