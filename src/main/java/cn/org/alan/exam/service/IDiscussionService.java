package cn.org.alan.exam.service;

import cn.org.alan.exam.model.entity.Discussion;
import cn.org.alan.exam.model.form.discussion.DiscussionForm;
import cn.org.alan.exam.model.vo.discussion.DiscussionDetailVo;
import cn.org.alan.exam.model.vo.discussion.PageDiscussionVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;


public interface IDiscussionService extends IService<Discussion> {

    

    
    Discussion createDiscussion(DiscussionForm discussionForm);


    
    int deleteDiscussion(Integer id);

    
    Page<PageDiscussionVo> getOwnerDiscussions(String title, Integer gradeId, Integer currentPage, Integer size);

    
    DiscussionDetailVo getDiscussionDetail(Integer id);

    


    
    Page<PageDiscussionVo> pageDiscussionByGrade(String title, Integer currentPage, Integer size);

}
