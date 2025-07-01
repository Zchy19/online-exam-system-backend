package cn.org.alan.exam.service;

import cn.org.alan.exam.common.result.Result;
import org.springframework.web.multipart.MultipartFile;


public interface IFileService {

    
    Result<String> uploadImage(MultipartFile file);
}
