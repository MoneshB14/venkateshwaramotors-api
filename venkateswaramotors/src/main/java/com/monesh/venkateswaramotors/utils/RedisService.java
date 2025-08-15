package com.monesh.venkateswaramotors.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class RedisService {

    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;
    
    // Fallback in-memory storage when Redis is not available
    private final Map<String, String> inMemoryStorage = new ConcurrentHashMap<>();
    private final Map<String, Long> expirationTimes = new ConcurrentHashMap<>();

    public void setValue(String key, String value, long timeout, TimeUnit timeUnit) {
        if (redisTemplate != null) {
            try {
                redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
            } catch (Exception e) {
                log.warn("Redis unavailable, using in-memory storage for key: {}", key);
                setValueInMemory(key, value, timeout, timeUnit);
            }
        } else {
            log.warn("Redis not configured, using in-memory storage for key: {}", key);
            setValueInMemory(key, value, timeout, timeUnit);
        }
    }

    public String getValue(String key) {
        if (redisTemplate != null) {
            try {
                return redisTemplate.opsForValue().get(key);
            } catch (Exception e) {
                log.warn("Redis unavailable, using in-memory storage for key: {}", key);
                return getValueFromMemory(key);
            }
        } else {
            return getValueFromMemory(key);
        }
    }

    public void deleteValue(String key) {
        if (redisTemplate != null) {
            try {
                redisTemplate.delete(key);
            } catch (Exception e) {
                log.warn("Redis unavailable, deleting from in-memory storage for key: {}", key);
                deleteValueFromMemory(key);
            }
        } else {
            deleteValueFromMemory(key);
        }
    }

    public boolean hasKey(String key) {
        if (redisTemplate != null) {
            try {
                return Boolean.TRUE.equals(redisTemplate.hasKey(key));
            } catch (Exception e) {
                log.warn("Redis unavailable, checking in-memory storage for key: {}", key);
                return hasKeyInMemory(key);
            }
        } else {
            return hasKeyInMemory(key);
        }
    }
    
    // In-memory fallback methods
    private void setValueInMemory(String key, String value, long timeout, TimeUnit timeUnit) {
        inMemoryStorage.put(key, value);
        long expirationTime = System.currentTimeMillis() + timeUnit.toMillis(timeout);
        expirationTimes.put(key, expirationTime);
    }
    
    private String getValueFromMemory(String key) {
        // Check if key exists and hasn't expired
        if (hasKeyInMemory(key)) {
            return inMemoryStorage.get(key);
        }
        return null;
    }
    
    private void deleteValueFromMemory(String key) {
        inMemoryStorage.remove(key);
        expirationTimes.remove(key);
    }
    
    private boolean hasKeyInMemory(String key) {
        if (!inMemoryStorage.containsKey(key)) {
            return false;
        }
        
        // Check if expired
        Long expirationTime = expirationTimes.get(key);
        if (expirationTime != null && System.currentTimeMillis() > expirationTime) {
            // Remove expired key
            deleteValueFromMemory(key);
            return false;
        }
        
        return true;
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