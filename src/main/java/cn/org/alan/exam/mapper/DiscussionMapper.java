package cn.org.alan.exam.mapper;

import cn.org.alan.exam.model.entity.Discussion;
import cn.org.alan.exam.model.vo.discussion.DiscussionDetailVo;
import cn.org.alan.exam.model.vo.discussion.PageDiscussionVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface DiscussionMapper extends BaseMapper<Discussion> {
    
    Page<PageDiscussionVo> selectOwnerPage(Page<PageDiscussionVo> page, Integer userId, String title, Integer gradeId);

    
    DiscussionDetailVo selectDetail(Integer id);

    
    Page<PageDiscussionVo> selectDiscussionByGradePage(Page<PageDiscussionVo> page, String title, Integer gradeId);
}
