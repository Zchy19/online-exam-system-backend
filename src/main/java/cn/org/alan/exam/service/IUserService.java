package cn.org.alan.exam.service;

import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.model.entity.User;
import cn.org.alan.exam.model.form.user.UserForm;
import cn.org.alan.exam.model.vo.user.UserVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;


public interface IUserService extends IService<User> {

    
    Result<String> createUser(UserForm userForm);

    
    Result<String> updatePassword(UserForm userForm);

    
    Result<String> deleteBatchByIds(String ids);

    
    Result<String> importUsers(MultipartFile file);

    
    Result<UserVO> info();

    
    Result<String> joinGrade(String code);

    
    Result<IPage<UserVO>> pagingUser(Integer pageNum, Integer pageSize, Integer gradeId, String realName);


    
    Result<String> uploadAvatar(MultipartFile file);

}
