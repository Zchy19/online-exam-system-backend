package cn.org.alan.exam.mapper;

import cn.org.alan.exam.model.entity.Repo;
import cn.org.alan.exam.model.vo.repo.RepoListVO;
import cn.org.alan.exam.model.vo.repo.RepoVO;
import cn.org.alan.exam.model.vo.exercise.ExerciseRepoVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface RepoMapper extends BaseMapper<Repo> {

    
    IPage<RepoVO> pagingRepo(@Param("page") IPage<RepoVO> page, 
                             @Param("title") String title,
                             @Param("userId") Integer userId,
                             @Param("categoryId") Integer categoryId);

    
    IPage<ExerciseRepoVO> selectRepo(IPage<ExerciseRepoVO> page,
                                     String title, 
                                     List<Integer> userList,
                                     Integer categoryId);

    
    List<RepoListVO> selectRepoList(String repoTitle, int userId);

    
    Page<Repo> selectUserExerciseRecord(Page<Repo> repoPage, Integer userId, String repoName);

}
