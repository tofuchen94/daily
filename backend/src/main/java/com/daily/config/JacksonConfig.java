package com.daily.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * Jackson 全局配置：Long 序列化为 String。
 * 防止 JavaScript Number 精度丢失（JS 最大安全整数 2^53-1，约 16 位，
 * 而 MyBatis-Plus 雪花 ID 为 19 位）。
 */
@JsonComponent
public class JacksonConfig {

    public static class LongToStringSerializer extends JsonSerializer<Long> {
        @Override
        public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeString(value.toString());
            }
        }
    }
}
