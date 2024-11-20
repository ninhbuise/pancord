package il.panda.pancord.service;

import il.panda.pancord.exception.DoesNotExist;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.UUID;

public interface StreamService {

    @Transactional(readOnly = true)
    StreamingResponseBody m3u8Index(String bucket, UUID videoId) throws DoesNotExist;
}
