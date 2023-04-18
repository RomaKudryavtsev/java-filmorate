package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class FilmorateApplication {
	//http://localhost:8080/swagger-ui/
	public static void main(String[] args) {
		SpringApplication.run(FilmorateApplication.class, args);
	}

}
