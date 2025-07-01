package cn.org.alan.exam.service.impl;

import cn.org.alan.exam.common.exception.ServiceRuntimeException;
import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.mapper.*;
import cn.org.alan.exam.model.entity.Category;
import cn.org.alan.exam.model.entity.Question;
import cn.org.alan.exam.model.entity.Repo;
import cn.org.alan.exam.model.vo.repo.RepoListVO;
import cn.org.alan.exam.model.vo.repo.RepoVO;
import cn.org.alan.exam.model.vo.exercise.ExerciseRepoVO;
import cn.org.alan.exam.service.ICategoryService;
import cn.org.alan.exam.service.IRepoService;
import cn.org.alan.exam.utils.SecurityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class RepoServiceImpl extends ServiceImpl<RepoMapper, Repo> implements IRepoService {
    @Resource
    private RepoMapper repoMapper;
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private UserGradeMapper userGradeMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ICategoryService categoryService;

    @Override
    public Result<String> addRepo(Repo repo) {
        
        if (repo.getCategoryId() != null) {
            Category category = categoryService.getById(repo.getCategoryId());
            if (category == null) {
                return Result.failed("分类不存在");
            }
        }
        
        int row = repoMapper.insert(repo);
        if (row > 0) {
            return Result.success("新增题库成功");
        }
        throw new ServiceRuntimeException("添加题库条数<1");
    }

    @Override
    public Result<String> updateRepo(Repo repo, Integer id) {
        
        if (repo.getCategoryId() != null) {
            Category category = categoryService.getById(repo.getCategoryId());
            if (category == null) {
                return Result.failed("分类不存在");
            }
        }
        
        
        LambdaUpdateWrapper<Repo> updateWrapper = new LambdaUpdateWrapper<Repo>()
                .eq(Repo::getId, id)
                .set(Repo::getTitle, repo.getTitle())
                .set(Repo::getIsExercise, repo.getIsExercise())
                .set(repo.getCategoryId() != null, Repo::getCategoryId, repo.getCategoryId());
        int row = repoMapper.update(updateWrapper);
        if (row > 0) {
            return Result.success("修改题库成功");
        }
        throw new ServiceRuntimeException("修改题库条数<1");
    }

    @Override
    @Transactional
    public Result<String> deleteRepoById(Integer id) {
        
        LambdaUpdateWrapper<Question> wrapper = new LambdaUpdateWrapper<Question>()
                .eq(Question::getRepoId, id)
                .set(Question::getRepoId, null);
        questionMapper.update(wrapper);
        
        boolean result = this.removeById(id);
        if (result) {
            return Result.success("删除题库成功");
        }
        throw new ServiceRuntimeException("删除题库条数<1");
    }

    @Override
    public Result<List<RepoListVO>> getRepoList(String repoTitle) {
        List<RepoListVO> list;
        Integer roleCode = SecurityUtil.getRoleCode();
        Integer userId = SecurityUtil.getUserId();
        if (roleCode == 2) {
            list = repoMapper.selectRepoList(repoTitle, userId);
        } else {
            list = repoMapper.selectRepoList(repoTitle, 0);
        }
        return Result.success("根据用户id获取自己的题库获取成功", list);
    }

    @Override
    public Result<IPage<RepoVO>> pagingRepo(Integer pageNum, Integer pageSize, String title, Integer categoryId) {
        IPage<RepoVO> page = new Page<>(pageNum, pageSize);
        Integer roleCode = SecurityUtil.getRoleCode();
        Integer userId = SecurityUtil.getUserId();
        if (roleCode == 2) {
            page = repoMapper.pagingRepo(page, title, userId, categoryId);
        } else {
            
            page = repoMapper.pagingRepo(page, title, 0, categoryId);
        }
        
        
        List<RepoVO> records = page.getRecords();
        for (RepoVO vo : records) {
            
            if (vo.getCategoryId() != null) {
                
                Category category = categoryService.getById(vo.getCategoryId());
                if (category != null) {
                    vo.setCategoryName(category.getName());
                }
            }
            
            
            LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<>();
            questionWrapper.eq(Question::getRepoId, vo.getId());
            int count = questionMapper.selectCount(questionWrapper).intValue();
            vo.setQuestionCount(count);
        }
        
        return Result.success("题库分页查询成功", page);
    }

    @Override
    public Result<IPage<ExerciseRepoVO>> getRepo(Integer pageNum, Integer pageSize, String title, Integer categoryId) {
        IPage<ExerciseRepoVO> page = new Page<>(pageNum, pageSize);
        
        Integer gradeId = SecurityUtil.getGradeId();
        
        List<Integer> adminList = userMapper.getAdminList();
        
        List<Integer> userList = userGradeMapper.getUserListByGradeId(gradeId);
        userList.addAll(adminList);
        
        page = repoMapper.selectRepo(page, title, userList, categoryId);
        
        
        List<ExerciseRepoVO> records = page.getRecords();
        for (ExerciseRepoVO vo : records) {
            
            if (vo.getCategoryId() != null) {
                
                Category category = categoryService.getById(vo.getCategoryId());
                if (category != null) {
                    
                    vo.setCategoryName(category.getName());
                    
                    
                    if (category.getParentId() != null && category.getParentId() > 0) {
                        vo.setParentCategoryId(category.getParentId());
                        
                        
                        Category parentCategory = categoryService.getById(category.getParentId());
                        if (parentCategory != null) {
                            vo.setParentCategoryName(parentCategory.getName());
                        }
                    }
                }
            }
        }
        
        return Result.success("分页获取可刷题库列表成功", page);
    }
    
    @Override
    public Result<IPage<RepoVO>> getReposByCategory(Integer categoryId, Integer pageNum, Integer pageSize) {
        
        List<Integer> categoryIds = new ArrayList<>();
        categoryIds.add(categoryId);
        
        
        LambdaQueryWrapper<Category> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.eq(Category::getParentId, categoryId);
        List<Category> childCategories = categoryService.list(categoryWrapper);
        if (!childCategories.isEmpty()) {
            List<Integer> childIds = childCategories.stream()
                    .map(Category::getId)
                    .collect(Collectors.toList());
            categoryIds.addAll(childIds);
        }
        
        
        Page<Repo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Repo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Repo::getCategoryId, categoryIds)
               .orderByDesc(Repo::getCreateTime);
        
        
        Integer userId = SecurityUtil.getUserId();
        Integer roleCode = SecurityUtil.getRoleCode();
        if (roleCode == 2) {
            wrapper.eq(Repo::getUserId, userId);
        }
        
        IPage<Repo> repoPage = page(page, wrapper);
        
        
        IPage<RepoVO> result = repoPage.convert(repo -> {
            RepoVO vo = new RepoVO();
            BeanUtils.copyProperties(repo, vo);
            
            
            if (repo.getCategoryId() != null) {
                Category category = categoryService.getById(repo.getCategoryId());
                if (category != null) {
                    vo.setCategoryName(category.getName());
                }
            }
            
            return vo;
        });
        
        return Result.success("根据分类查询题库成功", result);
    }
}