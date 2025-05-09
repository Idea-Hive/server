package Idea.Idea_Hive;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = "file:.env")
public class IdeaHiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdeaHiveApplication.class, args);
	}

}
