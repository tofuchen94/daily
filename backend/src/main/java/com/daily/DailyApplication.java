package com.daily;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
@MapperScan("com.daily.mapper")
public class DailyApplication {
    public static void main(String[] args) throws Exception {
        // SQLite 不会自动创建父目录，提前创建
        Files.createDirectories(Path.of("data"));
        SpringApplication.run(DailyApplication.class, args);
    }
}
