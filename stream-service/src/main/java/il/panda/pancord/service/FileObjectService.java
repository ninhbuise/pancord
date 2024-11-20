package il.panda.pancord.service;

import il.panda.pancord.exception.DoesNotExist;
import il.panda.pancord.models.entity.FileObject;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;
import java.util.UUID;

public interface FileObjectService {
    FileObject register(String bucket, MultipartFile file, String title, String path);

    FileObject findByUUID(UUID id) throws DoesNotExist;

    List<FileObject> findAllFileInBucket(String bucketName);

    void removeByUUID(UUID id);
}
