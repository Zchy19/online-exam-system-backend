package cn.org.alan.exam.mapper;

import cn.org.alan.exam.model.entity.UserExamsScore;
import cn.org.alan.exam.model.vo.answer.UncorrectedUserVO;
import cn.org.alan.exam.model.vo.score.ExportScoreVO;
import cn.org.alan.exam.model.vo.score.GradeScoreVO;
import cn.org.alan.exam.model.vo.score.UserScoreVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;


public interface UserExamsScoreMapper extends BaseMapper<UserExamsScore> {

    
    IPage<GradeScoreVO> scoreStatistics(IPage<GradeScoreVO> page, Integer gradeId, String examTitle, Integer userId,
                                        Integer roleId, List<Integer> gradeIdList);

    
    IPage<UserScoreVO> pagingScore(IPage<UserScoreVO> page, Integer gradeId, Integer examId, String realName);

    
    List<ExportScoreVO> selectScores(Integer examId, Integer gradeId);

    
    IPage<UncorrectedUserVO> uncorrectedUser(IPage<UncorrectedUserVO> page, Integer examId, String realName);

}
