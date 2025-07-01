package cn.org.alan.exam.mapper;

import cn.org.alan.exam.model.entity.UserBook;
import cn.org.alan.exam.model.vo.userbook.UserPageBookVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;


public interface UserBookMapper extends BaseMapper<UserBook> {

    
    int addUserBookList(List<UserBook> userBookArrayList);

    
    Page<UserPageBookVO> selectPageVo(Page<UserPageBookVO> page, String examName, Integer userId);

}
