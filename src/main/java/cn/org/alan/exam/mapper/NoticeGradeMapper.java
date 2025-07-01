package cn.org.alan.exam.mapper;

import cn.org.alan.exam.model.entity.NoticeGrade;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;


public interface NoticeGradeMapper extends BaseMapper<NoticeGrade> {

    
    int addNoticeGrade(Integer noticeId, List<Integer> gradeIdList);

    
    Integer delNoticeGrade(Integer noticeId);

    
    List<Integer> getNoticeIdList(Integer gradeId);

    
    List<Integer> getGradeList(Integer noticeId);

    
    void deleteNoticeGrade(List<Integer> noticeIds);

}
