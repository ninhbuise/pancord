package il.panda.pancord.service.impl;

import il.panda.pancord.exception.DoesNotExist;
import il.panda.pancord.models.entity.FileObject;
import il.panda.pancord.models.record.FileProperties;
import il.panda.pancord.repository.FileObjectRepository;
import il.panda.pancord.service.FileObjectService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FileObjectServiceImpl implements FileObjectService {

    private FileObjectRepository repository;

    @Override
    public FileObject register(String bucket, MultipartFile file, String title, String path) {
        var fileObject = FileObject
                .builder()
                .bucketName(bucket)
                .originFileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .title(title)
                .path(path)
                .build();
        return this.repository.save(fileObject);
    }

    @Override
    public FileObject findByUUID(UUID id) throws DoesNotExist {
        return this.repository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new DoesNotExist("target file does not exist!"));
    }

    @Override
    public List<FileObject> findAllFileInBucket(String bucketName) {
        return this.repository.findAllByBucketNameOrderByPathAsc(bucketName);
    }

    @Override
    public FileProperties getFileProperties(UUID id) throws DoesNotExist {
        var fileObject = this.repository.findById(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new DoesNotExist("target file does not exist!"));
        return new FileProperties(fileObject.getOriginFileName(), fileObject.getContentType(), fileObject.getSize());
    }

    @Override
    public void removeByUUID(UUID id) {
        this.repository.deleteById(id);
    }
}
