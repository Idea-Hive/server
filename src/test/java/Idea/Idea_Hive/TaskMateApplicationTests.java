package Idea.Idea_Hive;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;

@SpringBootTest
@PropertySource(value = "file:.env")
class TaskMateApplicationTests {

	@Test
	void contextLoads() {
	}

}
