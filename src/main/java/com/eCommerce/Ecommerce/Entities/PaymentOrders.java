package com.eCommerce.Ecommerce.Entities;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class PaymentOrders {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long amount;
    private PaymentMethodStatus orderStatus = PaymentMethodStatus.PENDING;
    private PaymentMethod paymentMethod;
    private String paymentLinkId;
    
    @ManyToOne
    private User user;
    
    @OneToMany
    private Set<Orders> orders = new HashSet<>();
}
