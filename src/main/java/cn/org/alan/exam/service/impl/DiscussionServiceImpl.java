package cn.org.alan.exam.service.impl;

import cn.org.alan.exam.converter.DiscussionConverter;
import cn.org.alan.exam.mapper.DiscussionMapper;
import cn.org.alan.exam.mapper.ReplyMapper;
import cn.org.alan.exam.model.entity.Discussion;
import cn.org.alan.exam.model.form.discussion.DiscussionForm;
import cn.org.alan.exam.model.vo.discussion.DiscussionDetailVo;
import cn.org.alan.exam.model.vo.discussion.PageDiscussionVo;
import cn.org.alan.exam.service.IDiscussionService;
import cn.org.alan.exam.utils.SecurityUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
public class DiscussionServiceImpl extends ServiceImpl<DiscussionMapper, Discussion> implements IDiscussionService {

    @Resource
    private DiscussionConverter discussionConverter;
    @Resource
    private ReplyMapper replyMapper;

    @Override
    public Discussion createDiscussion(DiscussionForm discussionForm) {
        
        Discussion discussion = discussionConverter.formToEntity(discussionForm);
        
        Integer userId = SecurityUtil.getUserId();
        
        discussion.setUserId(userId);
        int inserted = baseMapper.insert(discussion);
        if (inserted > 0) {
            return discussion;
        }
        throw new RuntimeException("创建讨论失败");
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDiscussion(Integer id) {
        
        Discussion discussion = baseMapper.selectById(id);
        if (discussion == null) {
            throw new RuntimeException("该id无对应的讨论");
        }
        
        int deleted = baseMapper.deleteById(id);
        

        if (deleted > 0) {
            
            return id;
        }
        throw new RuntimeException("删除讨论失败");
    }

    @Override
    public Page<PageDiscussionVo> getOwnerDiscussions(String title, Integer gradeId, Integer currentPage, Integer size) {
        Page<PageDiscussionVo> page = new Page<>(currentPage, size);
        return baseMapper.selectOwnerPage(page, SecurityUtil.getUserId(), title, gradeId);
    }


    @Override
    public DiscussionDetailVo getDiscussionDetail(Integer id) {
        return baseMapper.selectDetail(id);
    }

    @Override
    public Page<PageDiscussionVo> pageDiscussionByGrade(String title, Integer currentPage, Integer size) {
        Page<PageDiscussionVo> page = new Page<>(currentPage, size);
        return baseMapper.selectDiscussionByGradePage(page, title, SecurityUtil.getGradeId());
    }


}
