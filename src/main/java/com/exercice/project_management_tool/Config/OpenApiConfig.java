package com.exercice.project_management_tool.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI projectManagementOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Project Management Tool - API REST")
                .version("1.0.0")
                .description(
                        "API de gestion de projets collaboratifs: projets, membres, tâche, historique des modifications et notifications"));
    }
}
