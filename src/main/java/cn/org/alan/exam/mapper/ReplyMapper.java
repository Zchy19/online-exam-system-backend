package cn.org.alan.exam.mapper;

import cn.org.alan.exam.model.entity.Reply;
import cn.org.alan.exam.model.vo.reply.ReplyVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface ReplyMapper extends BaseMapper<Reply> {
    
    List<ReplyVo> selectReplies(Integer discussionId, Integer userId, Integer orderBy);

    
    List<ReplyVo> selectChildReplies(Integer replyId);

}
