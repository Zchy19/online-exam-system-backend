package cn.org.alan.exam.service.impl;

import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.mapper.CategoryMapper;
import cn.org.alan.exam.model.entity.Category;
import cn.org.alan.exam.model.vo.category.CategoryVO;
import cn.org.alan.exam.service.ICategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    @Override
    public Result<String> addCategory(Category category) {
        category.setCreateTime(new Date());
        save(category);
        return Result.success("添加分类成功");
    }

    @Override
    public Result<String> updateCategory(Category category, Integer id) {
        category.setId(id);
        updateById(category);
        return Result.success("修改分类成功");
    }

    @Override
    public Result<String> deleteCategory(Integer id) {
        
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, id);
        long count = count(wrapper);
        if (count > 0) {
            return Result.failed("该分类下有子分类，不能删除");
        }
        
        
        
        
        removeById(id);
        return Result.success("删除分类成功");
    }

    @Override
    public Result<List<CategoryVO>> getCategoryTree() {
        
        List<Category> allCategories = list();
        
        
        List<CategoryVO> result = buildCategoryTree(allCategories, 0);
        
        return Result.success("获取分类树成功", result);
    }

    @Override
    public Result<List<CategoryVO>> getFirstLevelCategories() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, 0)
               .orderByAsc(Category::getSort);
        
        List<Category> categories = list(wrapper);
        List<CategoryVO> result = categories.stream().map(this::convertToVO).collect(Collectors.toList());
        
        return Result.success("获取一级分类成功", result);
    }

    @Override
    public Result<List<CategoryVO>> getChildCategories(Integer parentId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, parentId)
               .orderByAsc(Category::getSort);
        
        List<Category> categories = list(wrapper);
        List<CategoryVO> result = categories.stream().map(this::convertToVO).collect(Collectors.toList());
        
        return Result.success("获取子分类成功", result);
    }
    
    
    private List<CategoryVO> buildCategoryTree(List<Category> categories, Integer parentId) {
        List<CategoryVO> result = new ArrayList<>();
        
        for (Category category : categories) {
            if (category.getParentId().equals(parentId)) {
                CategoryVO vo = convertToVO(category);
                vo.setChildren(buildCategoryTree(categories, category.getId()));
                result.add(vo);
            }
        }
        
        return result;
    }
    
    
    private CategoryVO convertToVO(Category category) {
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }
}