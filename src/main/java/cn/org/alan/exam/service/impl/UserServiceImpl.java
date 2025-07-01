package cn.org.alan.exam.service.impl;

import cn.org.alan.exam.common.exception.ServiceRuntimeException;
import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.converter.UserConverter;
import cn.org.alan.exam.mapper.*;
import cn.org.alan.exam.model.entity.Grade;
import cn.org.alan.exam.model.entity.User;
import cn.org.alan.exam.model.form.user.UserForm;
import cn.org.alan.exam.model.vo.user.UserVO;
import cn.org.alan.exam.service.IFileService;
import cn.org.alan.exam.service.IQuestionService;
import cn.org.alan.exam.service.IUserService;
import cn.org.alan.exam.utils.DateTimeUtil;
import cn.org.alan.exam.utils.SecurityUtil;
import cn.org.alan.exam.utils.excel.ExcelUtils;
import cn.org.alan.exam.utils.file.FileService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;



@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private HttpServletRequest request;
    @Resource
    private UserConverter userConverter;
    @Resource
    private GradeMapper gradeMapper;
    @Resource
    private UserGradeMapper userGradeMapper;
    @Resource
    private IFileService fileService;


    
    @Override
    public Result<String> createUser(UserForm userForm) {
        
        userForm.setPassword(new BCryptPasswordEncoder().encode("123456"));
        
        Integer roleCode = SecurityUtil.getRoleCode();
        
        if (roleCode == 2) {
            userForm.setRoleId(1);
        }
        if(userForm.getRoleId()==2&&userForm.getGradeId()!=null){
            throw new ServiceRuntimeException("教师无法设置单一班级");
        }
        
        if (userForm.getRoleId() == null || userForm.getRoleId() == 0) {
            throw new ServiceRuntimeException("未选择用户角色");
        }
        User user = userConverter.fromToEntity(userForm);
        
        userMapper.insert(user);
        return Result.success("用户创建成功");

    }

    @Override
    public Result<String> updatePassword(UserForm userForm) {
        Integer userId = SecurityUtil.getUserId();
        if (!userForm.getNewPassword().equals(userForm.getCheckedPassword())) {
            throw new ServiceRuntimeException("两次密码不一致");
        }
        if (!new BCryptPasswordEncoder()
                .matches(userForm.getOriginPassword(), userMapper.selectById(userId).getPassword())) {
            throw new ServiceRuntimeException("旧密码错误");
        }
        
        userForm.setPassword(new BCryptPasswordEncoder().encode(userForm.getNewPassword()));
        userForm.setId(userId);
        
        User user = userConverter.fromToEntity(userForm);
        
        int updated = userMapper.updateById(user);
        
        if (updated > 0) {
            stringRedisTemplate.delete("token:" + request.getSession().getId());
            return Result.success("修改成功，请重新登录");
        }
        throw new ServiceRuntimeException("旧密码错误");

    }

    @Override
    @Transactional
    public Result<String> deleteBatchByIds(String ids) {
        List<Integer> userIds = Arrays.stream(ids.split(",")).map(Integer::parseInt).collect(java.util.stream.Collectors.toList());
        List<Integer> adminList = userMapper.getAdminList();
        
        boolean containsAdminId = userIds.stream().anyMatch(adminList::contains);
        if(containsAdminId){
            throw new ServiceRuntimeException("无法删除管理员用户");
        }
        if (userIds.isEmpty()) {
            throw new ServiceRuntimeException("删除数据库时未传入用户Id");
        }
        Integer row = userMapper.deleteBatchIds(userIds);
        if (row < 1) {
            throw new ServiceRuntimeException("删除数据库时失败，条数<1");
        }
        return Result.success("删除成功");
    }

    @SneakyThrows(Exception.class)
    @Override
    @Transactional
    public Result<String> importUsers(MultipartFile file) {
        
        if (!ExcelUtils.isExcel(Objects.requireNonNull(file.getOriginalFilename()))) {
            throw new ServiceRuntimeException("文件类型必须是xls或xlsx");
        }
        
        List<UserForm> list = ExcelUtils.readMultipartFile(file, UserForm.class);
        
        list.forEach(userForm -> {
            
            userForm.setPassword(new BCryptPasswordEncoder().encode("123456"));
            userForm.setCreateTime(DateTimeUtil.getDateTime());
            if (userForm.getRoleId() == null) {
                
                userForm.setRoleId(1);
            }
        });
        
        if (list.size() > 300) {
            throw new ServiceRuntimeException("表中最多存放300条数据");
        }
        userMapper.insertBatchUser(userConverter.listFromToEntity(list));
        return Result.success("用户导入成功");
    }

    
    @Override
    public Result<UserVO> info() {
        
        Integer userId = SecurityUtil.getUserId();
        UserVO userVo = userMapper.info(userId);
        
        userVo.setPassword(null);
        return Result.success("获取用户信息成功", userVo);
    }

    
    @Override
    public Result<String> joinGrade(String code) {
        
        Integer userId = SecurityUtil.getUserId();
        LambdaQueryWrapper<Grade> wrapper = new LambdaQueryWrapper<Grade>().eq(Grade::getCode, code);
        Grade grade = gradeMapper.selectOne(wrapper);
        if (Objects.isNull(grade)) {
            throw new ServiceRuntimeException("班级口令不存在");
        }
        User user = new User();
        user.setId(userId);
        user.setGradeId(grade.getId());
        int updated = userMapper.updateById(user);
        if (updated > 0) {
            return Result.success("加入班级：" + grade.getGradeName() + "成功");
        }
        throw new ServiceRuntimeException("加入班级失败,加入数据库时失败");
    }

    @Override
    public Result<IPage<UserVO>> pagingUser(Integer pageNum, Integer pageSize, Integer gradeId, String realName) {
        IPage<UserVO> page = new Page<>(pageNum, pageSize);
        Integer userId = SecurityUtil.getUserId();
        Integer roleCode = SecurityUtil.getRoleCode();
        if (roleCode == 2) {
            
            List<Integer> gradeIdList = userGradeMapper.getGradeIdListByUserId(userId);
            if(gradeIdList.isEmpty()){
                throw new ServiceRuntimeException("教师还没加入班级暂无数据");
            }
            page = userMapper.pagingUser(page, gradeId, realName, userId, 1, gradeIdList);
        } else {
            
            page = userMapper.pagingUser(page, gradeId, realName, userId, null, null);
        }
        return Result.success("分页获取用户信息成功", page);
    }

    @Transactional
    @Override
    public Result<String> uploadAvatar(MultipartFile file) {
        
        Integer userId = SecurityUtil.getUserId();
        Result<String> result = fileService.uploadImage(file);
        if (result.getCode() == 0) {
            throw new ServiceRuntimeException("图片上传失败,上传图片代码code为0");
        }
        
        String url = result.getData();
        User user = new User();
        user.setId(userId);
        user.setAvatar(url);
        Integer row = userMapper.updateById(user);
        if (row > 0) {
            return Result.success("上传成功", url);
        }
        throw new ServiceRuntimeException("图片上传失败,修改用户表头像地址条数<=0");
    }


}
