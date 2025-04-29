
package com.example.mainbot.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String botUsername;
    private String botToken;
    private String serverIp;

    private boolean isActive;
    private LocalDate nextPaymentDate;
    private LocalDateTime lastPingAt;
}
