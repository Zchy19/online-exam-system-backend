package cn.org.alan.exam.service;

import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.model.entity.Repo;
import cn.org.alan.exam.model.vo.repo.RepoListVO;
import cn.org.alan.exam.model.vo.repo.RepoVO;
import cn.org.alan.exam.model.vo.exercise.ExerciseRepoVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface IRepoService extends IService<Repo> {

    
    Result<String> addRepo(Repo repo);

    
    Result<String> updateRepo(Repo repo, Integer id);

    
    Result<String> deleteRepoById(Integer id);

    
    Result<List<RepoListVO>> getRepoList(String repoTitle);

    
    Result<IPage<RepoVO>> pagingRepo(Integer pageNum, Integer pageSize, String title, Integer categoryId);

    
    Result<IPage<ExerciseRepoVO>> getRepo(Integer pageNum, Integer pageSize, String title, Integer categoryId);
    
    
    Result<IPage<RepoVO>> getReposByCategory(Integer categoryId, Integer pageNum, Integer pageSize);
    
}
