package cn.org.alan.exam.controller;

import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.model.form.exercise.ExerciseFillAnswerFrom;
import cn.org.alan.exam.model.vo.question.QuestionVO;
import cn.org.alan.exam.model.vo.exercise.AnswerInfoVO;
import cn.org.alan.exam.model.vo.exercise.ExerciseRepoVO;
import cn.org.alan.exam.model.vo.exercise.QuestionSheetVO;
import cn.org.alan.exam.service.IExerciseRecordService;
import cn.org.alan.exam.service.IRepoService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;


@Api(tags = "刷题管理相关接口")
@RestController
@RequestMapping("/api/exercises")
@Validated
public class ExerciseController {

    @Resource
    private IExerciseRecordService iExerciseRecordService;
    @Resource
    private IRepoService iRepoService;

    
    @ApiOperation("获取试题Id列表")
    @GetMapping("/{repoId}")
    @PreAuthorize("hasAnyAuthority('role_student')")
    public Result<List<QuestionSheetVO>> getQuestion(@PathVariable("repoId") Integer repoId,
                                                     @Min(value = 1, message = "试题类型最小值应为1")
                                                     @Max(value = 4, message = "试题类型最大值应为4")
                                                     @Nullable
                                                     @RequestParam(value = "quType", required = false) Integer quType) {
        return iExerciseRecordService.getQuestionSheet(repoId, quType);
    }


    
    @ApiOperation("填充答案，并返回试题信息")
    @PostMapping("/fillAnswer")
    @PreAuthorize("hasAnyAuthority('role_student')")
    public Result<QuestionVO> fillAnswer(@RequestBody ExerciseFillAnswerFrom exerciseFillAnswerFrom) {
        return iExerciseRecordService.fillAnswer(exerciseFillAnswerFrom);
    }

    
    @ApiOperation("分页获取可刷题库列表")
    @GetMapping("/getRepo")
    @PreAuthorize("hasAnyAuthority('role_student')")
    public Result<IPage<ExerciseRepoVO>> getRepo(
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "categoryId", required = false) Integer categoryId) {
        return iRepoService.getRepo(pageNum, pageSize, title, categoryId);
    }

    
    @ApiOperation("获取单题详情，没有答案")
    @GetMapping("/question/{id}")
    @PreAuthorize("hasAnyAuthority('role_student')")
    public Result<QuestionVO> getSingle(@PathVariable("id") Integer id) {
        return iExerciseRecordService.getSingle(id);
    }

    
    @ApiOperation("获取用户回答详情")
    @GetMapping("/answerInfo/{repoId}/{quId}")
    @PreAuthorize("hasAnyAuthority('role_student')")
    public Result<AnswerInfoVO> getAnswerInfo(@PathVariable("repoId") Integer repoId, @PathVariable("quId") Integer quId) {
        return iExerciseRecordService.getAnswerInfo(repoId, quId);
    }
}
