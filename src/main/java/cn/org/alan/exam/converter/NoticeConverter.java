package cn.org.alan.exam.converter;

import cn.org.alan.exam.model.entity.Notice;
import cn.org.alan.exam.model.form.notice.NoticeForm;
import cn.org.alan.exam.model.vo.notice.NoticeVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;


@Component
@Mapper(componentModel="spring")
public interface NoticeConverter {

    Notice formToEntity(NoticeForm noticeForm);

    Page<NoticeVO> pageEntityToVo(Page<Notice> noticePage);

    NoticeVO NoticeToNoticeVO(Notice notice);
}
