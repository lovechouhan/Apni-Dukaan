package com.eCommerce.Ecommerce.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddItemRequest {


    private Long productId;
    private int quantity;
    private String size;
    private int price;
}
