package il.panda.pancord.controller;

import il.panda.pancord.models.entity.FileObject;
import il.panda.pancord.models.record.UploadFileRequest;
import il.panda.pancord.service.FileObjectService;
import il.panda.pancord.service.MinioService;
import il.panda.pancord.utils.Utils;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/media/stream/api/v1")
public class MediaController {

    @Value("${minio.bucket.name}")
    private String bucketPublic;

    @Value("${minio.private.bucket.name}")
    private String bucketPrivate;

    private final MinioService minioService;

    private final FileObjectService fileObjectService;

    public MediaController(MinioService minioService, FileObjectService fileObjectService) {
        this.minioService = minioService;
        this.fileObjectService = fileObjectService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@Valid UploadFileRequest request) {
        try {
            var path = this.minioService.uploadFile(bucketPublic, request.folder(), request.file());
            var fileObject = this.fileObjectService.register(bucketPublic, request.file(), request.title(), path);
            log.info("saved file info {}", fileObject);
            return Utils.appendResponse(HttpStatus.OK, Utils.UPLOAD_SUCCESS, STR."{UUID: \{ fileObject.getId() }}" );
        } catch (Exception e) {
            log.error("file {} - {} Occurrence error: {}", request.file().getOriginalFilename(), Utils.UPLOAD_FAIL, e.getMessage());
            return Utils.appendResponse(HttpStatus.BAD_REQUEST, Utils.UPLOAD_FAIL, STR."{originFile: \{ request.file().getOriginalFilename() }}" );
        }
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> download(@RequestParam(value = "id") UUID id) {
        try {
            var fileObject = this.fileObjectService.findByUUID(id);
            InputStream fileStream = minioService.downloadFile(bucketPublic, fileObject.getPath());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, STR."attachment; filename=\"\{ fileObject.getOriginFileName() }\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(fileStream));
        } catch (Exception e) {
            log.error("UUID: {} - {}, occurrence error: {}", id, Utils.DOWNLOAD_FAIL, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
