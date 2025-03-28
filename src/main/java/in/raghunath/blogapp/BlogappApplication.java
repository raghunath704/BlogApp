package in.raghunath.blogapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "in.raghunath.blogapp.repo")
public class BlogappApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogappApplication.class, args);
	}

}
