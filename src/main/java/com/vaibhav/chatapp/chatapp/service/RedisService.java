package com.vaibhav.chatapp.chatapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void setValue(String key, Object value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration); // expires in 5 minutes
    }

    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    public boolean keyExists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key, 1);
    }

    public void expire(String key, Duration duration) {
        redisTemplate.expire(key, duration);
    }
}
