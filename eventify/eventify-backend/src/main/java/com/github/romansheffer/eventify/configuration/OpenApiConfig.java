package com.github.romansheffer.eventify.configuration;


import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenApiCustomizer globalErrorResponsesCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation -> {

                    Schema<?> errorSchema = new Schema<>().$ref("#/components/schemas/ErrorResponse");
                    Content errorContent = new Content().addMediaType("application/json",
                            new MediaType().schema(errorSchema));

                    if (!operation.getResponses().containsKey("400")) {
                        operation.getResponses().addApiResponse("400",
                                new ApiResponse().description("Неверный запрос (Ошибка валидации)").content(errorContent));
                    }

                    if (!operation.getResponses().containsKey("403")) {
                        operation.getResponses().addApiResponse("403",
                                new ApiResponse().description("Доступ запрещен (Недостаточно прав)").content(errorContent));
                    }

                    if (!operation.getResponses().containsKey("409")) {
                        operation.getResponses().addApiResponse("409",
                                new ApiResponse().description("Конфликт параллельного изменения данных").content(errorContent));
                    }
                })
        );
    }
}