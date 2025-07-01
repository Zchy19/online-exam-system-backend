package cn.org.alan.exam.controller;

import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.model.entity.Category;
import cn.org.alan.exam.model.vo.category.CategoryVO;
import cn.org.alan.exam.service.ICategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@Api(tags = "题库分类管理相关接口")
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Resource
    private ICategoryService categoryService;

    
    @PostMapping
    @ApiOperation("添加分类")
    @PreAuthorize("hasAnyAuthority('role_teacher','role_admin')")
    public Result<String> addCategory(@Validated @RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    
    @PutMapping("/{id}")
    @ApiOperation("修改分类")
    @PreAuthorize("hasAnyAuthority('role_teacher','role_admin')")
    public Result<String> updateCategory(@Validated @RequestBody Category category, @PathVariable("id") Integer id) {
        return categoryService.updateCategory(category, id);
    }

    
    @DeleteMapping("/{id}")
    @ApiOperation("删除分类")
    @PreAuthorize("hasAnyAuthority('role_teacher','role_admin')")
    public Result<String> deleteCategory(@PathVariable("id") Integer id) {
        return categoryService.deleteCategory(id);
    }

    
    @GetMapping("/tree")
    @ApiOperation("获取分类树")
    @PreAuthorize("hasAnyAuthority('role_student','role_teacher','role_admin')")
    public Result<List<CategoryVO>> getCategoryTree() {
        return categoryService.getCategoryTree();
    }

    
    @GetMapping("/first-level")
    @ApiOperation("获取一级分类")
    @PreAuthorize("hasAnyAuthority('role_student','role_teacher','role_admin')")
    public Result<List<CategoryVO>> getFirstLevelCategories() {
        return categoryService.getFirstLevelCategories();
    }

    
    @GetMapping("/children/{parentId}")
    @ApiOperation("获取子分类")
    @PreAuthorize("hasAnyAuthority('role_student','role_teacher','role_admin')")
    public Result<List<CategoryVO>> getChildCategories(@PathVariable("parentId") Integer parentId) {
        return categoryService.getChildCategories(parentId);
    }
}