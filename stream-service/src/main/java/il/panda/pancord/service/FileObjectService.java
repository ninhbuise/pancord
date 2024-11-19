package il.panda.pancord.service;

import il.panda.pancord.models.entity.FileObject;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileObjectService {
    FileObject register(String bucket, MultipartFile file, String title, String path);

    FileObject findByUUID(UUID id);
}
