package cn.org.alan.exam.common.handler;

import cn.org.alan.exam.common.exception.ServiceRuntimeException;
import cn.org.alan.exam.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.validation.ConstraintViolationException;
import java.sql.SQLIntegrityConstraintViolationException;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    
    @ExceptionHandler(ServiceRuntimeException.class)
    public Result<String> handleException(ServiceRuntimeException e) {
        String message = e.getMessage();
        log.error("接口调用异常: {}", message);
        return Result.failed(message);
    }

    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e.getClass());
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return Result.failed(message);
    }

    
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        log.error(e.getMessage(), e.getClass());
        return Result.failed("重复");
    }

    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e.getClass());
        return Result.failed("请求参数无法解析");
    }

    
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<String> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e.getClass());
        return Result.failed(e.getParameterName() + "为必填项");
    }

    
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<String> handleDuplicateKeyException(DuplicateKeyException e) {
        String name = e.getMessage().split(":")[2].split(" ")[3];
        log.error(e.getMessage(), e.getClass());
        return Result.failed("主键冲突" + name + "已存在");
    }

    
    @ExceptionHandler(AccessDeniedException.class)
    public Result<String> handleAccessDeniedException(AccessDeniedException e) {
        log.error(e.getMessage(), e.getClass());
        return Result.failed("你没有该资源的访问权限");
    }


    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<String> handlerMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error(e.getMessage(), e.getClass());
        return Result.failed("文件太大，最大上传5MB");
    }

    
    @ExceptionHandler(MissingServletRequestPartException.class)
    public Result<String> handlerMissingServletRequestPartException(MissingServletRequestPartException e) {
        log.error(e.getMessage(), e.getClass());
        return Result.failed("没有获取到文件");
    }


    
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<String> handleConstraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage(), e.getClass());
        return Result.failed(e.getMessage());
    }
}
