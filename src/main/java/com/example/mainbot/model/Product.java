package com.example.mainbot.model;

import com.example.mainbot.model.template.AbsEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends AbsEntity {

    private String name;
    private String description;
    private Double price;
    private Integer availableQuantity;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
