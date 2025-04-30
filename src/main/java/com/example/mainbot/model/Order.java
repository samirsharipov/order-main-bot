package com.example.mainbot.model;

import com.example.mainbot.model.template.AbsEntity;
import com.example.mainbot.model.template.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends AbsEntity {
    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Integer quantity;
    private String deliveryAddress;
    private boolean isPickup;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
