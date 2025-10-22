package com.eCommerce.Ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SupportController {

    @GetMapping("/user/support")
    public String supportPage() {
        return "support";
    }
}
