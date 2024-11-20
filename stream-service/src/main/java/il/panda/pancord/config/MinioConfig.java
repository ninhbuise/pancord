package il.panda.pancord.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Data
@Log4j2
@Configuration
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access.key}")
    private String accessKey;

    @Value("${minio.access.secret}")
    private String secretKey;

    @Value("${minio.bucket.name}")
    private String bucketPublic;

    @Value("${minio.private.bucket.name}")
    private String bucketPrivate;

    @Bean
    public MinioClient minioClient() throws Exception {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.MINUTES)
                .build();

        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .httpClient(httpClient)
                .credentials(accessKey, secretKey)
                .build();
        initBucket(minioClient, bucketPublic);
        initBucket(minioClient, bucketPrivate);
        return minioClient;
    }

    private void initBucket(MinioClient minioClient, String bucket) throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            log.info("successfully created bucket {}!", bucket);
        }
    }
}
