package cn.org.alan.exam.mapper;

import cn.org.alan.exam.model.entity.User;
import cn.org.alan.exam.model.form.count.ClassCountResult;
import cn.org.alan.exam.model.vo.user.UserVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Repository;


import java.util.List;


public interface UserMapper extends BaseMapper<User> {

    
    Integer removeUserGrade(List<Integer> userIds);

    
    Integer insertBatchUser(List<User> list);

    
    UserVO info(Integer userId);

    
    IPage<UserVO> pagingUser(IPage<UserVO> page, Integer gradeId, String realName, Integer userId, Integer roleId, List<Integer> gradeIdList);

    
    Integer userExitGrade(Integer gradeId, Integer userId);

    
    List<Integer> getAdminList();

}
