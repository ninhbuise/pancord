package il.panda.pancord.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class StreamConfig {
  @Value("${server.stream.prefix}")
  private String prefix;
}
