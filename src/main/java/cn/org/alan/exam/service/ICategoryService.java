package cn.org.alan.exam.service;

import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.model.entity.Category;
import cn.org.alan.exam.model.vo.category.CategoryVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface ICategoryService extends IService<Category> {

    
    Result<String> addCategory(Category category);

    
    Result<String> updateCategory(Category category, Integer id);

    
    Result<String> deleteCategory(Integer id);

    
    Result<List<CategoryVO>> getCategoryTree();

    
    Result<List<CategoryVO>> getFirstLevelCategories();

    
    Result<List<CategoryVO>> getChildCategories(Integer parentId);
}