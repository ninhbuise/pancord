package il.panda.pancord.controller;

import il.panda.pancord.service.MinioService;
import il.panda.pancord.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Log4j2
@RestController
@RequestMapping("/media/stream/api/v1")
public class MediaController {

    @Value("${minio.bucket.name}")
    private String bucketPublic;

    @Value("${minio.private.bucket.name}")
    private String bucketPrivate;

    private final MinioService minioService;

    public MediaController(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("folder") String folder,
                                        @RequestParam("file") MultipartFile file) {
        try {
            String fileName = this.minioService.uploadFile(bucketPublic, folder, file);
            return Utils.appendResponse(HttpStatus.OK, Utils.UPLOAD_SUCCESS, STR."{fileName: \{ fileName }}" );
        } catch (Exception e) {
            log.error("file {} - {} Occurrence error: {}", file.getOriginalFilename(), Utils.UPLOAD_FAIL, e.getMessage());
            return Utils.appendResponse(HttpStatus.BAD_REQUEST, Utils.UPLOAD_FAIL, STR."{originFile: \{ file.getOriginalFilename() }}" );
        }
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> download(@RequestParam(value = "filename") String filename) {
        try {
            InputStream fileStream = minioService.downloadFile(bucketPublic, filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, STR."attachment; filename=\"\{filename}\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(fileStream));
        } catch (Exception e) {
            log.error("file {} - {} Occurrence error: {}", filename, Utils.DOWNLOAD_FAIL, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
