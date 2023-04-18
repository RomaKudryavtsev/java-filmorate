package ru.yandex.practicum.filmorate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .tags(new Tag("Reviews", "Adding reviews to films"),
                        new Tag("Users", "Functionality related to users"),
                        new Tag("Films", "Functionality related to films"));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Filmorate")
                .description("A social network that helps users choose movies based on what their friends " +
                        "are watching and how they rate them.")
                .version("2.0.0")
                .build();
    }
}