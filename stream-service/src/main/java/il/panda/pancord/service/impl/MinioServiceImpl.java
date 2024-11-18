package il.panda.pancord.service.impl;

import il.panda.pancord.service.MinioService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Log4j2
@Service
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    @Autowired
    public MinioServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public String uploadFile(String bucketName, String folder, MultipartFile file) throws Exception {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = STR. "\{ folder }/\{ timestamp }_\{ file.getOriginalFilename() }" ;
        InputStream inputStream = file.getInputStream();
        var res = minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .stream(inputStream, inputStream.available(), -1)
                .contentType(file.getContentType())
                .build());
        log.info("upload file {} to {}", res.object(), res.bucket());
        return fileName;
    }

    @Override
    public InputStream downloadFile(String bucketName, String filename) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build()
            );
        } catch (Exception e) {
            log.error("Occurrence error when get file {}, {}", STR. "/\{ bucketName }/\{ filename }" , e.getMessage());
            return null;
        }
    }

    @Override
    public boolean removeFile(String bucketName, String folder, List<String> filenames) {
        try {
            for (String filename : filenames) {
                String deleteFile = STR. "/\{ folder }/\{ filename }" ;
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(deleteFile)
                        .build());
                log.info("{} has been deleted", deleteFile);
            }
            return true;
        } catch (Exception e) {
            log.error("Occurrence error when delete files {}, {}", filenames, e.getMessage());
            return false;
        }
    }
}
