package com.monesh.venkateswaramotors.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void setValue(String key, String value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void setOTP(String email, String otp) {
        String key = "otp:" + email;
        setValue(key, otp, 5, TimeUnit.MINUTES); // OTP expires in 5 minutes
    }

    public String getOTP(String email) {
        String key = "otp:" + email;
        return getValue(key);
    }

    public void deleteOTP(String email) {
        String key = "otp:" + email;
        deleteValue(key);
    }

    public boolean validateOTP(String email, String otp) {
        String storedOTP = getOTP(email);
        if (storedOTP != null && storedOTP.equals(otp)) {
            deleteOTP(email); // Delete OTP after successful validation
            return true;
        }
        return false;
    }
} 