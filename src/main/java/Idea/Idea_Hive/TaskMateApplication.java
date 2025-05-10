package Idea.Idea_Hive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@PropertySource(value = "file:.env")
public class TaskMateApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskMateApplication.class, args);
	}

}
