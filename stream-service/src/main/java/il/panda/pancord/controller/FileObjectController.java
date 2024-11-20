package il.panda.pancord.controller;

import il.panda.pancord.config.MinioConfig;
import il.panda.pancord.exception.DoesNotExist;
import il.panda.pancord.models.record.UploadFileRequest;
import il.panda.pancord.models.record.VideoRequest;
import il.panda.pancord.service.FileObjectService;
import il.panda.pancord.service.MinioService;
import il.panda.pancord.service.StreamService;
import il.panda.pancord.utils.Utils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.util.UUID;

@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/file/api/v1")
public class FileObjectController {

    private final MinioConfig minioConfig;

    private final MinioService minioService;

    private final FileObjectService fileObjectService;

    private final StreamService streamService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@Valid UploadFileRequest request) {
        try {
            var path = this.minioService.uploadFile(minioConfig.getBucketPublic(), request.folder(), request.file());
            var fileObject = this.fileObjectService.register(minioConfig.getBucketPublic(), request.file(), request.title(), path);
            log.info("saved file info {}", fileObject);
            return Utils.appendResponse(HttpStatus.OK, Utils.UPLOAD_SUCCESS, STR. "{UUID: \{ fileObject.getId() }}" );
        } catch (Exception e) {
            log.error("file {} - {} Occurrence error: {}", request.file().getOriginalFilename(), Utils.UPLOAD_FAIL, e.getMessage());
            return Utils.appendResponse(HttpStatus.BAD_REQUEST, Utils.UPLOAD_FAIL, STR. "{originFile: \{ request.file().getOriginalFilename() }}" );
        }
    }

    @GetMapping(value = "/download/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> download(@PathVariable(value = "id") UUID id) {
        try {
            var fileObject = this.fileObjectService.findByUUID(id);
            InputStream fileStream = minioService.downloadFile(minioConfig.getBucketPublic(), fileObject.getPath());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, STR. "attachment; filename=\"\{ fileObject.getOriginFileName() }\"" )
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(fileStream));
        } catch (DoesNotExist e) {
            log.error("UUID: {} - {}, occurrence error: {}", id, Utils.DOWNLOAD_FAIL, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") UUID id) {
        try {
            var fileObject = this.fileObjectService.findByUUID(id);
            this.minioService.removeFile(minioConfig.getBucketPublic(), fileObject.getPath());
            this.fileObjectService.removeByUUID(id);
            return Utils.appendResponse(HttpStatus.OK, Utils.DELETE_SUCCESS, STR. "{UUID: \{ fileObject.getId() }}" );
        } catch (Exception e) {
            log.error("UUID: {} - {}, occurrence error: {}", id, Utils.DELETE_FAIL, e.getMessage());
            return Utils.appendResponse(HttpStatus.BAD_REQUEST, Utils.DELETE_FAIL, STR. "{UUID: \{ id }}" );
        }
    }

    @GetMapping("/bucket/{name}")
    public ResponseEntity<?> getBucketFileObject(@PathVariable(value = "name") String name) {
        var fileObjects = this.fileObjectService.findAllFileInBucket(name);
        return Utils.appendResponse(HttpStatus.OK, null, fileObjects);
    }

    @GetMapping(path = "/stream/{id}/index.m3u8", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> stream(@Valid @ParameterObject VideoRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/vnd.apple.mpegurl");
            headers.set("Content-Disposition", "attachment;filename=index.m3u8");
            StreamingResponseBody body = this.streamService.m3u8Index(minioConfig.getBucketPublic(), UUID.fromString(request.id()));
            return new ResponseEntity<>(body, headers, HttpStatus.OK);
        } catch (DoesNotExist e) {
            return ResponseEntity.notFound().build();
        }
    }
}
