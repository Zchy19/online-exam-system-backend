package cn.org.alan.exam.controller;

import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.model.vo.record.ExamRecordDetailVO;
import cn.org.alan.exam.model.vo.record.ExamRecordVO;
import cn.org.alan.exam.model.vo.record.ExerciseRecordDetailVO;
import cn.org.alan.exam.model.vo.record.ExerciseRecordVO;
import cn.org.alan.exam.service.IExerciseRecordService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import javax.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Api(tags = "考试记录相关接口")
@RestController
@RequestMapping("/api/records")
public class RecordController {
    @Resource
    private IExerciseRecordService exerciseRecordService;

    
    @ApiOperation("分页查询已考试试卷")
    @GetMapping("/exam/paging")
    @PreAuthorize("hasAnyAuthority('role_teacher','role_admin','role_student')")
    public Result<IPage<ExamRecordVO>> getExamRecordPage(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                         @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                                         @RequestParam(value = "examName", required = false) String examName,
                                                         @RequestParam(value = "isASC", required = false, defaultValue = "false") Boolean isASC) {
        return exerciseRecordService.getExamRecordPage(pageNum, pageSize, examName, isASC);
    }

    
    @ApiOperation("查询试卷详情")
    @GetMapping("/exam/detail")
    @PreAuthorize("hasAnyAuthority('role_teacher','role_admin','role_student')")
    public Result<List<ExamRecordDetailVO>> getExamRecordDetail(@RequestParam(value = "examId",required = true) Integer examId,
                                                                @RequestParam(value = "userId",required = false)Integer userId) {
        return exerciseRecordService.getExamRecordDetail(examId,userId);
    }

    
    @ApiOperation("分页查询已考试刷题")
    @GetMapping("/exercise/paging")
    @PreAuthorize("hasAnyAuthority('role_teacher','role_admin','role_student')")
    public Result<IPage<ExerciseRecordVO>> getExerciseRecordPage(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                                 @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                                                 @RequestParam(value = "repoName", required = false) String repoName) {
        return exerciseRecordService.getExerciseRecordPage(pageNum, pageSize, repoName);
    }

    
    @ApiOperation("查询刷题详情")
    @GetMapping("/exercise/detail")
    @PreAuthorize("hasAnyAuthority('role_teacher','role_admin','role_student')")
    public Result<List<ExerciseRecordDetailVO>> getExerciseRecordDetail(@RequestParam("repoId") Integer exerciseId) {
        return exerciseRecordService.getExerciseRecordDetail(exerciseId);
    }
}
