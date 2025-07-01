package cn.org.alan.exam.utils.file.impl;

import cn.org.alan.exam.utils.file.FileService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;


@Service
@ConditionalOnProperty(name = "online-exam.storage.type", havingValue = "aliyun")
public class AliOSSUtil implements FileService {

    @Value("${oss.endpoint}")
    private String endpoint;
    @Value("${oss.access-key-id}")
    private String accessKeyId;
    @Value("${oss.access-key-secret}")
    private String accessKeySecret;
    @Value("${oss.bucket-name}")
    private String bucketName;

    
    @Override
    public String upload(MultipartFile file) throws IOException {
        
        InputStream inputStream = file.getInputStream();

        
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null : "上传文件时获取文件名失败，为null";
        String fileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));

        
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, fileName, inputStream);

        
        String url;
        if (endpoint.startsWith("http")) {
            url = endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + fileName;
        } else {
            url = "https://" + bucketName + "." + endpoint + "/" + fileName;
        }
        
        
        ossClient.shutdown();
        
        return url;
    }


    
    @Override
    public boolean isImage(String filename) {
        String lastName = filename.substring(filename.lastIndexOf(".") + 1);
        String[] lastnames = {"png", "jpg", "jpeg", "bmp"};
        return Arrays.asList(lastnames).contains(lastName);
    }

    
    @Override
    public boolean isOverSize(MultipartFile file) {
        return file.getSize() > 10 * 1024 * 1024;
    }
}
