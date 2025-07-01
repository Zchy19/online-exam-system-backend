package cn.org.alan.exam.controller;


import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.model.entity.Repo;
import cn.org.alan.exam.model.vo.repo.RepoListVO;
import cn.org.alan.exam.model.vo.repo.RepoVO;
import cn.org.alan.exam.service.IRepoService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import javax.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "题库管理相关接口")
@RestController
@RequestMapping("/api/repo")
public class RepoController {

    @Resource
    private IRepoService iRepoService;

    
    @PostMapping
    @ApiOperation("添加题库")
    @PreAuthorize("hasAnyAuthority('role_teacher','role_admin')")
    public Result<String> addRepo(@Validated @RequestBody Repo repo) {
        
        return iRepoService.addRepo(repo);
    }

    
    @ApiOperation("修改题库")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('role_teacher','role_admin')")
    public Result<String> updateRepo(@Validated @RequestBody Repo repo, @PathVariable("id") Integer id) {
        return iRepoService.updateRepo(repo, id);
    }

    
    @ApiOperation("根据题库id删除题库")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('role_teacher','role_admin')")
    public Result<String> deleteRepoById(@PathVariable("id") Integer id) {
        return iRepoService.deleteRepoById(id);
    }

    
    @ApiOperation("获取所有题库")
    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('role_teacher','role_admin')")
    public Result<List<RepoListVO>> getRepoList(@RequestParam(value = "repoTitle", required = false) String repoTitle) {
        return iRepoService.getRepoList(repoTitle);
    }

    
    @ApiOperation("分页查询题库")
    @GetMapping("/paging")
    @PreAuthorize("hasAnyAuthority('role_teacher','role_admin')")
    public Result<IPage<RepoVO>> pagingRepo(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                            @RequestParam(value = "title", required = false) String title,
                                            @RequestParam(value = "categoryId", required = false) Integer categoryId) {
        return iRepoService.pagingRepo(pageNum, pageSize, title, categoryId);
    }
    
    
    @ApiOperation("根据分类ID查询题库")
    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAnyAuthority('role_teacher','role_admin')")
    public Result<IPage<RepoVO>> getReposByCategory(
            @PathVariable("categoryId") Integer categoryId,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return iRepoService.getReposByCategory(categoryId, pageNum, pageSize);
    }

}
