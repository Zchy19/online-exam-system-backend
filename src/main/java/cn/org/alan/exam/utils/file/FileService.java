package cn.org.alan.exam.utils.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface FileService {
    
    public String upload(MultipartFile file) throws IOException;
    
    public boolean isImage(String filename);

    
    public boolean isOverSize(MultipartFile file);
}