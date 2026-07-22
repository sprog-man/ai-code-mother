package com.xiaoliu.aiCodeMother.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {
    /**
     * 将限流 Lua 脚本加载到 Spring 容器中
     */
    @Bean
    public DefaultRedisScript<Long> rateLimitScript() {
        DefaultRedisScript<Long> script=new DefaultRedisScript<>();
        // 加载 resources 目录下的 lua 脚本
        script.setLocation(new ClassPathResource("ratelimit.lua"));
        //设置脚本的返回类型为 Long
        //script.setResultType(Long.class)：
        //这一步是类型安全的保障。你在 Lua 脚本里写的是 return current 或者 return 0（都是数字）。
        // Java 是强类型语言，你必须提前告诉 Spring：“这个脚本跑完后，Redis 会给我返回一个 Long 类型的数字，你帮我转好，别给我返回个字符串或者字节数组。”
        script.setResultType(Long.class);
        return script;
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        // 1. 定义 Key 的序列化方式（通常保持 String 即可）
        RedisSerializationContext.SerializationPair<String> keySerializer =
                RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer());

        // 2. 定义 Value 的序列化方式（重点！改成 JSON）
        // 使用 GenericJackson2JsonRedisSerializer 可以自动处理多态类型（比如你的 BaseResponse<User>）
        RedisSerializationContext.SerializationPair<Object> valueSerializer =
                RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer());

        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(keySerializer)
                .serializeValuesWith(valueSerializer)
                // 可选：设置默认过期时间
                .entryTtl(Duration.ofHours(1));
    }
}
