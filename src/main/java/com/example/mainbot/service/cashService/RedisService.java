package com.example.mainbot.service.cashService;

import com.example.mainbot.model.Shop;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private static final String STATE_PREFIX = "user:state:";
    private static final String LANG_PREFIX = "user:lang:";
    private static final String ID_PREFIX = "user:id:";
    private static final String TEMP_DATA_PREFIX = "user:temp:";
    private static final long EXPIRE_DAYS = 7; // Ma'lumotlarni 7 kun saqlaymiz

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void saveState(Long chatId, String state) {
        String key = STATE_PREFIX + chatId;
        redisTemplate.opsForValue().set(key, state, Duration.ofDays(EXPIRE_DAYS));
    }

    public String getState(Long chatId) {
        return redisTemplate.opsForValue().get(STATE_PREFIX + chatId);
    }

    public void deleteState(Long chatId) {
        redisTemplate.delete(STATE_PREFIX + chatId);
    }

    public void saveLanguage(Long chatId, String language) {
        redisTemplate.opsForValue().set(LANG_PREFIX + chatId, language, EXPIRE_DAYS, TimeUnit.DAYS);
    }

    public String getLanguage(Long chatId) {
        String language = redisTemplate.opsForValue().get(LANG_PREFIX + chatId);
        return (language != null) ? language : "uz";
    }

    public void deleteLanguage(Long chatId) {
        redisTemplate.delete(LANG_PREFIX + chatId);
    }

    public void saveAdminId(Long chatId, String adminId) {
        redisTemplate.opsForValue().set(ID_PREFIX + chatId, adminId, EXPIRE_DAYS, TimeUnit.DAYS);
    }

    public String getAdminId(Long chatId) {
        return redisTemplate.opsForValue().get(ID_PREFIX + chatId);
    }

    public void saveTempData(Long chatId, String key, String value) {
        redisTemplate.opsForValue().set(TEMP_DATA_PREFIX + chatId + ":" + key, value, Duration.ofDays(EXPIRE_DAYS));
    }

    public String getTempData(Long chatId, String key) {
        return redisTemplate.opsForValue().get(TEMP_DATA_PREFIX + chatId + ":" + key);
    }

    public void deleteTempData(Long chatId, String key) {
        redisTemplate.delete(TEMP_DATA_PREFIX + chatId + ":" + key);
    }

    public void savePage(Long chatId, int page) {
        redisTemplate.opsForValue().set(TEMP_DATA_PREFIX + chatId + ":page", String.valueOf(page), Duration.ofDays(EXPIRE_DAYS));
    }

    public int getPage(Long chatId) {
        String page = redisTemplate.opsForValue().get(TEMP_DATA_PREFIX + chatId + ":page");
        return (page != null) ? Integer.parseInt(page) : 0;
    }

    public void deletePage(Long chatId) {
        redisTemplate.delete(TEMP_DATA_PREFIX + chatId + ":page");
    }

    public void saveTempShop(Long chatId, Shop shop) {
        try {
            String json = objectMapper.writeValueAsString(shop);
            redisTemplate.opsForValue().set(TEMP_DATA_PREFIX + chatId + ":shop", json, Duration.ofDays(EXPIRE_DAYS));
            System.out.println("✅ Redisga Shop saqlandi: key=" + TEMP_DATA_PREFIX + chatId + ":shop, value=" + json);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // logga yozing
        }
    }

    public Shop getTempShop(Long chatId) {
        String json = redisTemplate.opsForValue().get(TEMP_DATA_PREFIX + chatId + ":shop");
        if (json == null) return new Shop(); // Birinchi safar null bo‘ladi
        try {
            return objectMapper.readValue(json, Shop.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Shop(); // xato bo‘lsa yangi Shop
        }
    }

    public void deleteTempShop(Long chatId) {
        redisTemplate.delete(TEMP_DATA_PREFIX + chatId + ":shop");
    }
}
