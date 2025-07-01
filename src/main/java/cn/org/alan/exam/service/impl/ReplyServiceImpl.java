package cn.org.alan.exam.service.impl;

import cn.org.alan.exam.converter.ReplyConverter;
import cn.org.alan.exam.mapper.DiscussionMapper;
import cn.org.alan.exam.mapper.ReplyMapper;
import cn.org.alan.exam.model.entity.Discussion;
import cn.org.alan.exam.model.entity.Reply;
import cn.org.alan.exam.model.form.reply.ReplyForm;
import cn.org.alan.exam.model.vo.reply.ReplyVo;
import cn.org.alan.exam.service.ILikeService;
import cn.org.alan.exam.service.IReplyService;
import cn.org.alan.exam.utils.SecurityUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;


@Service
public class ReplyServiceImpl extends ServiceImpl<ReplyMapper, Reply> implements IReplyService {

    @Resource
    private DiscussionMapper discussionMapper;
    @Resource
    private ReplyConverter replyConverter;
    @Resource
    private ILikeService likeService;

    @Override
    public Reply addReply(ReplyForm replyForm) {
        
        Reply reply = replyConverter.formToEntity(replyForm);
        
        if (reply.getParentId() == null) {
            reply.setParentId(-1);
        }
        
        reply.setUserId(SecurityUtil.getUserId());
        int inserted = baseMapper.insert(reply);
        if (inserted > 0) {
            return reply;
        }
        throw new RuntimeException("回复失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteReply(Integer id) {
        
        Reply reply = baseMapper.selectById(id);
        
        if (reply == null) {
            throw new RuntimeException("该id无对应的回复");
        }
        
        Integer currentUserId = SecurityUtil.getUserId();
        
        if (!Objects.equals(reply.getUserId(), currentUserId)) {
            
            Discussion discussion = discussionMapper.selectById(reply.getDiscussionId());
            
            if (discussion != null && !Objects.equals(discussion.getUserId(), currentUserId)) {
                throw new RuntimeException("无权删除他人回复");
            }
        }
        
        int deleted = baseMapper.deleteById(id);
        
        
        
        
        deleted += likeService.deleteLikeByReplyId(id);
        if (deleted > 0) {
            return deleted;
        }
        throw new RuntimeException("删除回复失败");
    }

    @Override
    public List<ReplyVo> queryReplyByDiscussionId(Integer orderBy, Integer discussionId) {
        return baseMapper.selectReplies(discussionId, SecurityUtil.getUserId(), orderBy);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByDiscussionId(Integer discussionId) {
        
        LambdaUpdateWrapper<Reply> wrapper = new LambdaUpdateWrapper<Reply>()
                .eq(Reply::getDiscussionId, discussionId);
        
        int count = likeService.deleteLikeByDiscussionId(discussionId);
        count += baseMapper.delete(wrapper);
        return count;
    }


}
