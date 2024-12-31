package il.panda.pancord.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "FILE_OBJECT", indexes = {@Index(columnList = "CREATE_AT DESC")})
public class FileObject implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(unique = true, updatable = false, nullable = false)
    private UUID id;

    @Column(name = "TITLE", length = 256, nullable = false)
    private String title;

    @Column(name = "ORIGIN_FILE_NAME", length = 256, nullable = false)
    private String originFileName;

    @Column(name = "BUCKET_NAME", length = 60, nullable = false)
    private String bucketName;

    @Column(name = "PATH", length = 512, nullable = false)
    private String path;

    @Column(name = "CONTENT_TYPE", length = 256, nullable = false)
    private String contentType;

    @Column(name = "Size", length = 256)
    private long size;

    @Column(name = "CREATE_AT", nullable = false)
    private Instant createAt;

    @PrePersist
    private void prePersist() {
        createAt = Instant.now();
    }
}
