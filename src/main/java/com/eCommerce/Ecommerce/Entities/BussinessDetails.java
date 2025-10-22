package com.eCommerce.Ecommerce.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class BussinessDetails {

    @Column(name = "business_name", length = 100)
    private String businessName;

    @Column(name = "business_address", length = 200)
    private String businessAddress;

    @Column(name = "business_contact", length = 15)
    private String businessContact;

    @Column(name = "business_email", length = 100)
    private String businessEmail;

    @Column(name = "logo_url", length = 255)
    private String logo;

    @Column(name = "banner_url", length = 255)
    private String banner;

}
