package il.panda.pancord.models.record;

import il.panda.pancord.validation.FileName;
import org.springframework.web.multipart.MultipartFile;

public record UploadFileRequest(@FileName String title, String folder, MultipartFile file) {
}
