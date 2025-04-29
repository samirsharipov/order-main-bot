
package com.example.mainbot.service;

import com.example.mainbot.model.Shop;
import com.example.mainbot.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;

    public Shop addShop(Shop shop) {
        if (shop.getNextPaymentDate() == null) {
            shop.setNextPaymentDate(LocalDate.now().plusMonths(1));
        }
        shop.setIsActive(true);
        shop.setLastPingAt(LocalDateTime.now());
        return shopRepository.save(shop);
    }

    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    public void updatePing(String botUsername) {
        shopRepository.findByBotUsername(botUsername).ifPresent(shop -> {
            shop.setLastPingAt(LocalDateTime.now());
            shopRepository.save(shop);
        });
    }
}
