package com.eCommerce.Ecommerce.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Razorpay {

    @Id
    private Long id;
    private String currency = "INR";
    private String receiptId = "order_rcptid_11";
    private int amount;
}
