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
import java.util.UUID;

@Log4j2
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class StreamServiceImpl implements StreamService {

    private StreamConfig streamConfig;

    private FileObjectService fileObjectService;

    private MinioService minioService;

    @Override
    public StreamingResponseBody m3u8Index(String bucket, UUID videoId) throws DoesNotExist {
        String prefix = STR. "\{ streamConfig.getPrefix() }/\{ videoId }/" ;
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
                throw new IllegalArgumentException(STR. "stream video m3u8Index occurrence error \{e.getMessage()}");
            }
        };
    }

    @Override
    public StreamingResponseBody loadPartialMediaFile(String bucket, UUID videoId, long startPos, long endPos) throws DoesNotExist {
        if (startPos > endPos) throw new IllegalArgumentException(STR. "stream video partial in valid startPos \{startPos} can't be larger than endPos \{endPos}");
        var video = fileObjectService.findByUUID(videoId);
        return outputStream -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(minioService.downloadFile(bucket, video.getPath()))); outputStream) {
                long currentPos = 0;
                String line;
                while ((line = reader.readLine()) != null && currentPos <= endPos) {
                    currentPos += line.length() + 1; // +1 for the newline character
                    if (currentPos > startPos && currentPos <= endPos) {
                        outputStream.write(line.getBytes());
                        outputStream.write(System.lineSeparator().getBytes());
                    }
                }
                outputStream.flush();
            } catch (IOException e) {
                log.error("stream video partial occurrence error {}", e.getMessage());
                throw new IllegalArgumentException(STR. "stream video partial occurrence error \{e.getMessage()}");
            }
        };
    }
}
