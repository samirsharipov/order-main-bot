package com.example.mainbot.model;

import com.example.mainbot.model.template.AbsEntity;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends AbsEntity {

    private Long telegramUserId;
    private String fullName;
    private String username;
    private String phone;
}
