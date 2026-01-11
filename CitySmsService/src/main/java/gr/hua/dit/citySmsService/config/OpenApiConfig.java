package gr.hua.dit.citySmsService.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI (Swagger) documentation
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the global metadata for the API
     *
     * @return the {@link OpenAPI} definition
     */
    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("City SMS Service API")
                        .version("v1")
                        .description("Microservice for sending SMS notifications"));
    }

    /**
     * Groups the API endpoints for version 1
     *
     * @return the {@link GroupedOpenApi} for the v1 API
     */
    @Bean
    public GroupedOpenApi apiGroup() {
        return GroupedOpenApi.builder()
                .group("api")
                .packagesToScan("gr.hua.dit.citySmsService.web.rest")
                .pathsToMatch("/api/v1/**")
                .build();
    }
}
