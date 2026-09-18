package com.fam.attendance.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI famAttendanceOpenApi(
            @Value("${server.port:8080}") int serverPort) {
        return new OpenAPI()
                .info(new Info()
                        .title("FAM Attendance API")
                        .description("考勤後端 API（Postman 可匯入 /v3/api-docs）")
                        .version("v0"))
                .addServersItem(new Server()
                        .url("http://localhost:" + serverPort)
                        .description("Local"));
    }
}
