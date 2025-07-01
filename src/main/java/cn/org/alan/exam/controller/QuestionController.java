package cn.org.alan.exam.controller;


import cn.org.alan.exam.common.group.QuestionGroup;
import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.model.form.question.QuestionFrom;
import cn.org.alan.exam.model.vo.question.QuestionVO;
import cn.org.alan.exam.service.IFileService;
import cn.org.alan.exam.service.IQuestionService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import javax.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Api(tags = "试题管理相关接口")
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Resource
    private IQuestionService iQuestionService;

    @Resource
    private IFileService fileService;

    
    @ApiOperation("单题添加")
    @PostMapping("/single")
    @PreAuthorize("hasAnyAuthority('role_teacher','role_admin')")
    public Result<String> addSingleQuestion(@Validated(QuestionGroup.QuestionAddGroup.class) @RequestBody QuestionFrom questionFrom) {
        return iQuestionService.addSingleQuestion(questionFrom);
    }

    
    @ApiOperation("批量删除试题")
    @DeleteMapping("/batch/{ids}")
    @PreAuthorize("hasAnyAuthority('role_teacher','role_admin')")
    public Result<String> deleteBatchQuestion(@PathVariable("ids") String ids) {
        return iQuestionService.deleteBatchByIds(ids);
    }

    
    @ApiOperation("分页查询试题")
    @GetMapping("/paging")
    @PreAuthorize("hasAnyAuthority('role_teacher','role_admin')")
    public Result<IPage<QuestionVO>> pagingQuestion(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                    @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                                    @RequestParam(value = "content", required = false) String content,
                                                    @RequestParam(value = "repoId", required = false) Integer repoId,
                                                    @RequestParam(value = "type", required = false) Integer type) {
        return iQuestionService.pagingQuestion(pageNum, pageSize, content, type, repoId);
    }

    
    @ApiOperation("根据试题id获取单题详情")
    @GetMapping("/single/{id}")
    @PreAuthorize("hasAnyAuthority('role_teacher','role_admin')")
    public Result<QuestionVO> querySingle(@PathVariable("id") Integer id) {
        return iQuestionService.querySingle(id);
    }

    
    @ApiOperation("修改试题")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('role_teacher','role_admin')")
    public Result<String> updateQuestion(@PathVariable("id") Integer id, @RequestBody QuestionFrom questionFrom) {
        questionFrom.setId(id);
        return iQuestionService.updateQuestion(questionFrom);
    }

    
    @ApiOperation("批量导入试题")
    @PostMapping("/import/{id}")
    @PreAuthorize("hasAnyAuthority('role_teacher','role_admin')")
    public Result<String> importQuestion(@PathVariable("id") Integer id, @RequestParam("file") MultipartFile file) {
        return iQuestionService.importQuestion(id, file);
    }

    
    @ApiOperation("上传图片")
    @PostMapping("/uploadImage")
    @PreAuthorize("hasAnyAuthority('role_teacher','role_admin')")
    public Result<String> uploadImage(@RequestPart("file") MultipartFile file) {
        return fileService.uploadImage(file);
    }
}
