package gr.hua.dit.citySmsService.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("City SMS Service API")
                        .version("v1")
                        .description("Microservice for sending SMS notifications"));
    }

    @Bean
    public GroupedOpenApi apiGroup() {
        return GroupedOpenApi.builder()
                .group("api")
                .packagesToScan("gr.hua.dit.citySmsService.web.rest")
                .pathsToMatch("/api/v1/**")
                .build();
    }
}
