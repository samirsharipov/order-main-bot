package com.example.mainbot.model;

import com.example.mainbot.model.template.AbsEntity;
import jakarta.persistence.*;
import lombok.*;
import org.glassfish.grizzly.http.util.TimeStamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shop extends AbsEntity {

    private String name;
    private String botUsername;
    private String botToken;

    private String serverIp; // Agar kerak bo‘lsa
    private String ownerUserId; // Telegram foydalanuvchi ID (do‘kon egasi)

    @OneToMany(mappedBy = "shop")
    private List<Product> products;

    private LocalDate nextPaymentDate;
    private LocalDateTime lastPingAt;
}