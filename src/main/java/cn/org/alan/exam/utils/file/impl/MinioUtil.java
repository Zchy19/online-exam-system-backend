package cn.org.alan.exam.utils.file.impl;

import cn.org.alan.exam.utils.file.FileService;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;


@Service
@ConditionalOnProperty(name = "online-exam.storage.type", havingValue = "minio")
public class MinioUtil implements FileService {
    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.accesskey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String accessKeySecret;
    @Value("${minio.bucket}")
    private String bucketName;

    @Override
    public String upload(MultipartFile file) throws IOException {
        
        InputStream inputStream = file.getInputStream();

        
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null : "上传文件时获取文件名失败，为null";
        String fileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));

        
        try {
            MinioClient client = new MinioClient(endpoint, accessKey, accessKeySecret);

            client.putObject(bucketName, fileName, inputStream, new PutObjectOptions(inputStream.available(), -1));
        } catch (Exception e) {
            
            e.printStackTrace();
            return "";
        } finally {
            
            inputStream.close();
        }


        
        String url = endpoint + "/" + bucketName + "/" + fileName;

        
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
        return file.getSize() > 20 * 1024 * 1024;
    }
}