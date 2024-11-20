package il.panda.pancord.service.impl;

import il.panda.pancord.service.MinioService;
import io.minio.*;
import io.minio.messages.Item;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@AllArgsConstructor
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    @Override
    public String uploadFile(String bucketName, String folder, MultipartFile file) throws Exception {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String path = STR. "\{ folder }/\{ timestamp }_\{ file.getOriginalFilename() }" ;
        InputStream inputStream = file.getInputStream();
        var res = minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(path)
                .stream(inputStream, inputStream.available(), -1)
                .contentType(file.getContentType())
                .build());
        log.info("upload file {} to {}", res.object(), res.bucket());
        inputStream.close();
        return path;
    }

    @Override
    public InputStream downloadFile(String bucket, String path) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .build()
            );
        } catch (Exception e) {
            log.error("Occurrence error when get file {}, {}", STR. "/\{ bucket }/\{ path }" , e.getMessage());
            return null;
        }
    }

    @Override
    public void removeFile(String bucketName, String path) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(path)
                    .build());
            log.info("{} has been deleted", path);
        } catch (Exception e) {
            log.error("Occurrence error when delete files {}, {}", path, e.getMessage());
        }
    }

    @Override
    public List<String> listFiles(String bucket, String folderName) throws Exception {
        List<Item> results = new ArrayList<>();
        var items = minioClient.listObjects(ListObjectsArgs
                .builder()
                .bucket(bucket)
                .prefix(folderName)
                .recursive(false)
                .build());
        for (Result<Item> itemResult : items) {
            Item i = itemResult.get();
            if (i.isDir()) continue;
            results.add(i);
        }
        return results.stream().map(Item::objectName).toList();
    }
}
