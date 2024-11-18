package il.panda.pancord;

import il.panda.pancord.utils.Utils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableScheduling
@SpringBootApplication
public class PancordApplication {

	public static void main(String[] args) {
		SpringApplication.run(PancordApplication.class, args);
	}

	@GetMapping("api/v1/heath")
	public ResponseEntity<?> heath() {
		return Utils.appendResponse(HttpStatus.OK, "OK", null);
	}
}

