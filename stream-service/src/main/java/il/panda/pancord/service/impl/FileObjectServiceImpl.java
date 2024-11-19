package il.panda.pancord.service.impl;

import il.panda.pancord.models.entity.FileObject;
import il.panda.pancord.repository.FileObjectRepository;
import il.panda.pancord.service.FileObjectService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@AllArgsConstructor
@Service
public class FileObjectServiceImpl implements FileObjectService {

    private FileObjectRepository repository;

    @Override
    public FileObject register(String bucket, MultipartFile file, String title, String path) {
        var fileObject = FileObject
                .builder()
                .bucketName(bucket)
                .originFileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .title(title)
                .path(path)
                .build();
        return this.repository.save(fileObject);
    }

    @Override
    public FileObject findByUUID(UUID id) {
        return this.repository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("target file does not exist!"));
    }
}
