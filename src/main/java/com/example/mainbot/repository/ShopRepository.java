
package com.example.mainbot.repository;

import com.example.mainbot.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByBotUsername(String botUsername);
}
