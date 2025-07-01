package cn.org.alan.exam.service;

import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.model.entity.Like;
import cn.org.alan.exam.model.form.like.LikeForm;
import com.baomidou.mybatisplus.extension.service.IService;


public interface ILikeService extends IService<Like> {

    
    Result<String> doLike(LikeForm likeForm);

    
    Like queryLike(LikeForm likeForm);

    
    int deleteLike(Integer id);

    
    int deleteLikeByDiscussionId(Integer discussionId);

    
    int deleteLikeByReplyId(Integer replyId);
}
