package com.eCommerce.Ecommerce.Entities;

import lombok.Data;

@Data
public class PaymentInfo {

    private String paymentId;
    private String razorpayPaymentLinkId;
    private String razorpayPPaymentLinkReferenceId;
    private String razorpayPaymentLinkStatus;
    private String razorpayPaymentId;
    private PaymentStatus status;
}
