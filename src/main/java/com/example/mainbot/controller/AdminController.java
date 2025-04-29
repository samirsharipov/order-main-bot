
package com.example.mainbot.controller;

import com.example.mainbot.model.Shop;
import com.example.mainbot.service.ShopService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final ShopService shopService;

    @GetMapping("/api/shops")
    public List<Shop> getAllShops() {
        return shopService.getAllShops();
    }

    @PostMapping("/api/register-bot")
    public Shop registerBot(@RequestBody Shop shop) {
        return shopService.addShop(shop);
    }

    @PostMapping("/api/ping")
    public void pingBot(@RequestParam String botUsername) {
        shopService.updatePing(botUsername);
    }
}
