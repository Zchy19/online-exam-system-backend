package cn.org.alan.exam.common.handler;

import cn.org.alan.exam.utils.DateTimeUtil;
import cn.org.alan.exam.utils.SecurityUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;


@Component
@Slf4j
public class FiledFullHandler implements MetaObjectHandler {
    
    @Override
    public void insertFill(MetaObject metaObject) {
        
        Class<?> clazz = metaObject.getOriginalObject().getClass();
        Field[] fields = clazz.getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            
            if ("userId".equals(field.getName()) && (Objects.isNull(getFieldValByName("userId", metaObject)))) {
                log.info("user_id字段满足公共字段自动填充规则，已填充");
                this.strictInsertFill(metaObject, "userId", Integer.class, SecurityUtil.getUserId());

            }
            
            if ("createTime".equals(field.getName()) && (Objects.isNull(getFieldValByName("createTime", metaObject)))) {
                log.info("create_time字段满足公共字段自动填充规则，已填充");
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, DateTimeUtil.getDateTime());
            }
        });
    }

    
    @Override
    public void updateFill(MetaObject metaObject) {

    }
}
