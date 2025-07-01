package cn.org.alan.exam.service.impl;

import cn.org.alan.exam.common.exception.ServiceRuntimeException;
import cn.org.alan.exam.common.result.Result;
import cn.org.alan.exam.converter.QuestionConverter;
import cn.org.alan.exam.mapper.ExerciseRecordMapper;
import cn.org.alan.exam.mapper.OptionMapper;
import cn.org.alan.exam.mapper.QuestionMapper;
import cn.org.alan.exam.model.entity.ExerciseRecord;
import cn.org.alan.exam.model.entity.Option;
import cn.org.alan.exam.model.entity.Question;
import cn.org.alan.exam.model.form.question.QuestionExcelFrom;
import cn.org.alan.exam.model.form.question.QuestionFrom;
import cn.org.alan.exam.model.vo.question.QuestionVO;
import cn.org.alan.exam.service.IQuestionService;
import cn.org.alan.exam.utils.file.FileService;
import cn.org.alan.exam.utils.SecurityUtil;
import cn.org.alan.exam.utils.excel.ExcelUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;


@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements IQuestionService {

    @Resource
    private QuestionConverter questionConverter;
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private OptionMapper optionMapper;
    @Resource
    private ExerciseRecordMapper exerciseRecordMapper;

    @Override
    @Transactional
    public Result<String> addSingleQuestion(QuestionFrom questionFrom) {
        
        List<Option> options = questionFrom.getOptions();
        if (questionFrom.getQuType() != 4 && (Objects.isNull(options) || options.size() < 2)) {
            return Result.failed("非简答题的试题选项不能少于两个");
        }
        Question question = questionConverter.fromToEntity(questionFrom);
        
        questionMapper.insert(question);
        
        if (question.getQuType() == 4) {
            
            Option option = questionFrom.getOptions().get(0);
            option.setQuId(question.getId());
            optionMapper.insert(option);
        } else {
            
            
            options.forEach(option -> {
                option.setQuId(question.getId());
            });
            optionMapper.insertBatch(options);
        }
        return Result.success("单题添加成功");

    }

    @Override
    @Transactional
    public Result<String> deleteBatchByIds(String ids) {
        List<Integer> qIdList = Arrays.stream(ids.split(",")).map(Integer::parseInt).collect(java.util.stream.Collectors.toList());
        
        LambdaUpdateWrapper<ExerciseRecord> updateWrapper = new LambdaUpdateWrapper<ExerciseRecord>()
                .in(ExerciseRecord::getQuestionId, qIdList);
        exerciseRecordMapper.delete(updateWrapper);
        
        optionMapper.deleteBatchIds(qIdList);
        
        questionMapper.deleteBatchIds(qIdList);
        return Result.success("批量删除试题成功");
    }

    @Override
    public Result<IPage<QuestionVO>> pagingQuestion(Integer pageNum, Integer pageSize, String title, Integer type, Integer repoId) {
        IPage<QuestionVO> page = new Page<>(pageNum, pageSize);
        
        Integer userId = SecurityUtil.getUserId();
        Integer roleCode = SecurityUtil.getRoleCode();
        
        page = questionMapper.selectQuestionPage(page, userId, roleCode, title, type, repoId);
        return Result.success("分页查询试题成功", page);
    }

    @Override
    public Result<QuestionVO> querySingle(Integer id) {
        QuestionVO result = questionMapper.selectSingle(id);
        return Result.success("根据试题id获取单题详情成功", result);
    }

    @Override
    @Transactional
    public Result<String> updateQuestion(QuestionFrom questionFrom) {
        
        Question question = questionConverter.fromToEntity(questionFrom);
        questionMapper.updateById(question);
        
        List<Option> options = questionFrom.getOptions();
        for (Option option : options) {
            if (option.getIsDeleted() != null && option.getIsDeleted() == 1) {
                
                optionMapper.deleteById(option.getId());
            } else {
                
                optionMapper.updateById(option);
            }
        }
        return Result.success("修改试题成功");
    }

    @SneakyThrows(Exception.class)
    @Override
    @Transactional
    public Result<String> importQuestion(Integer id, MultipartFile file) {
        if (!ExcelUtils.isExcel(Objects.requireNonNull(file.getOriginalFilename()))) {
            throw new ServiceRuntimeException("该文件不是一个合法的Excel文件");
        }
        
        try {
            List<QuestionExcelFrom> questionExcelFroms = ExcelUtils.readMultipartFile(file, QuestionExcelFrom.class);
            
            List<QuestionFrom> list = QuestionExcelFrom.converterQuestionFrom(questionExcelFroms);
            
            for (QuestionFrom questionFrom : list) {
                Question question = questionConverter.fromToEntity(questionFrom);
                question.setRepoId(id);
                
                questionMapper.insert(question);
                
                List<Option> options = questionFrom.getOptions();
                final int[] count = {0};
                options.forEach(option -> {
                    
                    if (question.getQuType() == 4) {
                        option.setIsRight(1);
                    }
                    option.setSort(++count[0]);
                    option.setQuId(question.getId());
                });
                
                if (!options.isEmpty()) {
                    optionMapper.insertBatch(options);
                }
            }
            
            return Result.success("导入试题成功");
        } catch (ServiceRuntimeException e) {
            
            return Result.failed(e.getMessage());
        } catch (Exception e) {
            
            return Result.failed("导入试题失败：" + e.getMessage());
        }
    }

}
