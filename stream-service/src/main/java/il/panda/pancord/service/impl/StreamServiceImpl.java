package il.panda.pancord.service.impl;

import il.panda.pancord.config.StreamConfig;
import il.panda.pancord.exception.DoesNotExist;
import il.panda.pancord.service.FileObjectService;
import il.panda.pancord.service.MinioService;
import il.panda.pancord.service.StreamService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

@Log4j2
@Service
@AllArgsConstructor
public class StreamServiceImpl implements StreamService {

    private StreamConfig streamConfig;

    private FileObjectService fileObjectService;

    private MinioService minioService;

    @Override
    @Transactional(readOnly = true)
    public StreamingResponseBody m3u8Index(String bucket, UUID videoId) throws DoesNotExist {
        String prefix = STR. "\{ streamConfig.getPrefix() }/\{ videoId }/" ;
        try {
            var video = fileObjectService.findByUUID(videoId);
//            List<String> files = minioService.listFiles(bucket, video.getPath());
//            String target = files.stream()
//                    .filter((f) -> f.endsWith("index.m3u8"))
//                    .findFirst()
//                    .orElseThrow(() -> new DoesNotExist(STR. "Video file with UUID \{videoId} dose not exit!"));
            return outputStream -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(minioService.downloadFile(bucket, video.getPath()))); outputStream) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.endsWith(".ts")) line = prefix + line;
                        outputStream.write(line.getBytes());
                        outputStream.write(System.lineSeparator().getBytes());
                    }
                    outputStream.flush();
                } catch (IOException e) {
                    log.error("stream video m3u8Index occurrence error {}", e.getMessage());
                }
            };
        } catch (Exception e) {
            throw new DoesNotExist("cannot retrieve target m3u8!");
        }
    }
}