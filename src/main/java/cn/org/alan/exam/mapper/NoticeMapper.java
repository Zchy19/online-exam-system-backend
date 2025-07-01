package cn.org.alan.exam.mapper;

import cn.org.alan.exam.model.entity.Notice;
import cn.org.alan.exam.model.form.notice.NoticeForm;
import cn.org.alan.exam.model.vo.notice.NoticeVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;


public interface NoticeMapper extends BaseMapper<Notice> {

    
    Integer getIsPublic(Integer noticeId);

    
    Integer updateNotice(Integer noticeId, NoticeForm noticeForm);

    
    Page<NoticeVO> getNewNotice(Page<NoticeVO> page, List<Integer> teachIdList, List<Integer> noticeIdList, List<Integer> adminIdList);

    
    List<NoticeVO> getNotice(Integer userId, String title);

}
