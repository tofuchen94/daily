package com.daily.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI dailyOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("业绩日报系统 API")
                        .description("Daily Performance — 每日业绩记录、指标管理与报表生成接口")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("开发者"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
