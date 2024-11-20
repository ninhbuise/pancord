package il.panda.pancord.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface MinioService {

    String uploadFile(String bucketName, String folder, MultipartFile file) throws Exception;

    InputStream downloadFile(String bucketName, String filename);

    void removeFile(String bucketName, String path);

    List<String> listFiles(String bucket, String folderName) throws Exception;
}
