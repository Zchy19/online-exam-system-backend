package cn.org.alan.exam.service;

import cn.org.alan.exam.model.entity.Reply;
import cn.org.alan.exam.model.form.reply.ReplyForm;
import cn.org.alan.exam.model.vo.reply.ReplyVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface IReplyService extends IService<Reply> {

    
    Reply addReply(ReplyForm replyForm);

    
    Integer deleteReply(Integer id);

    
    List<ReplyVo> queryReplyByDiscussionId(Integer orderBy, Integer discussionId);

    
    int deleteByDiscussionId(Integer discussionId);

}
