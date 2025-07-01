package cn.org.alan.exam.service.impl;

import cn.org.alan.exam.common.exception.ServiceRuntimeException;
import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.converter.NoticeConverter;
import cn.org.alan.exam.mapper.*;
import cn.org.alan.exam.model.entity.Notice;
import cn.org.alan.exam.model.form.notice.NoticeForm;
import cn.org.alan.exam.model.vo.notice.NoticeVO;
import cn.org.alan.exam.service.INoticeService;
import cn.org.alan.exam.utils.SecurityUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;


@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements INoticeService {
    @Resource
    private NoticeMapper noticeMapper;
    @Resource
    private NoticeConverter noticeConverter;
    @Resource
    private NoticeGradeMapper noticeGradeMapper;
    @Resource
    private UserGradeMapper userGradeMapper;
    @Resource
    private UserMapper userMapper;

    
    @Override
    @Transactional
    public Result<String> addNotice(NoticeForm noticeForm) {
        String gradeIds = noticeForm.getGradeIds();
        
        noticeForm.setUserId(SecurityUtil.getUserId());
        
        Notice notice = noticeConverter.formToEntity(noticeForm);
        
        Integer roleCode = SecurityUtil.getRoleCode();
        if(roleCode==3){
            notice.setIsPublic(1);
        }
        
        if(noticeForm.getIsPublic()==0){
            if("".equals(gradeIds)|| gradeIds==null){
                throw new ServiceRuntimeException("公开班级必须添入班级");
            }
            int addNotionRowOther = noticeMapper.insert(notice);
            if (addNotionRowOther == 0) {
                throw new ServiceRuntimeException("添加公告失败");
            }
            
            Integer noticeId = notice.getId();
            List<Integer> gradeIdList = Arrays.stream(gradeIds.split(","))
                    .map(Integer::parseInt)
                    .collect(java.util.stream.Collectors.toList());
            int addNoticeGradeRow = noticeGradeMapper.addNoticeGrade(noticeId,gradeIdList);
            if (addNoticeGradeRow == 0) {
                throw new ServiceRuntimeException("添加公告条数=0失败");
            }
        }else{
            int addNotionRowAdmin = noticeMapper.insert(notice);
            if (addNotionRowAdmin == 0) {
                throw new ServiceRuntimeException("添加公告失败");
            }
        }
        return Result.success("添加公告成功");
    }

    
    @Override
    @Transactional
    public Result<String> deleteNotice(String ids) {
        
        List<Integer> noticeIds = Arrays.stream(ids.split(","))
                .map(Integer::parseInt)
                .collect(java.util.stream.Collectors.toList());
        
        noticeMapper.deleteBatchIds(noticeIds);
        noticeGradeMapper.deleteNoticeGrade(noticeIds);
        return Result.success("删除成功");
    }

    
    @Override
    @Transactional
    public Result<String> updateNotice(Integer noticeId, NoticeForm noticeForm) {
        Integer isPublic =  noticeMapper.getIsPublic(noticeId);
        String gradeIds = noticeForm.getGradeIds();
        List<Integer> gradeIsList = null;
        if(!"".equals(gradeIds)&&gradeIds!=null){
            gradeIsList = Arrays.stream(gradeIds.split(","))
                    .map(Integer::parseInt)
                    .collect(java.util.stream.Collectors.toList());
        }
        if(isPublic!=noticeForm.getIsPublic()&&isPublic==0){
            
            noticeMapper.updateNotice(noticeId,noticeForm);
            noticeGradeMapper.delNoticeGrade(noticeId);
        }else if(isPublic!=noticeForm.getIsPublic()&&isPublic==1){
            
            noticeMapper.updateNotice(noticeId,noticeForm);
            noticeGradeMapper.addNoticeGrade(noticeId,gradeIsList);
        }
        
        if(isPublic==noticeForm.getIsPublic()&&isPublic==1){
            
            
            noticeMapper.updateNotice(noticeId,noticeForm);
        }else if (isPublic==noticeForm.getIsPublic()&&isPublic==0){
            
            noticeMapper.updateNotice(noticeId,noticeForm);
            noticeGradeMapper.delNoticeGrade(noticeId);
            noticeGradeMapper.addNoticeGrade(noticeId,gradeIsList);
        }
        return Result.success("修改成功");
    }

    
    @Override
    public Result<IPage<NoticeVO>> getNotice(Integer pageNum, Integer pageSize, String title) {
        IPage<NoticeVO> page = new Page<>(pageNum, pageSize);
        Integer userId = SecurityUtil.getUserId();
        
        List<NoticeVO> record = noticeMapper.getNotice(userId,title);
        
        for(NoticeVO temp: record){
            List<Integer> gradeList = noticeGradeMapper.getGradeList(temp.getId());
            temp.setGradeIds(gradeList);
        }
        page.setRecords(record);
        return Result.success("查询成功", page);

    }

    
    @Override
    public Result<IPage<NoticeVO>> getNewNotice(Integer pageNum, Integer pageSize) {
        
        Page<NoticeVO> page = new Page<>(pageNum, pageSize);
        
        Integer gradeId = SecurityUtil.getGradeId();
        
        List<Integer> teachIdList= userGradeMapper.getUserListByGradeId(gradeId);
        
        List<Integer> noticeIdList = noticeGradeMapper.getNoticeIdList(gradeId);
        
        List<Integer> adminIdList = userMapper.getAdminList();
        
        page = noticeMapper.getNewNotice(page,teachIdList,noticeIdList,adminIdList);
        return Result.success("查询成功", page);
    }
}
