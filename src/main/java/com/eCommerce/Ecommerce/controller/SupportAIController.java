package com.eCommerce.Ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eCommerce.Ecommerce.Services.SupportService;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/support")
public class SupportAIController {

    @Autowired
    private SupportService supportService;

    @PostMapping("/query")
    public Flux<String> streamResponse(
            @RequestParam(value = "question") String question, Model model) {

        // consultant
        String websiteContext = "This is my website about fashion e-commerce, selling sarees, jewelry, and accessories. "
                +
                "The website has categories";

        if (question != null) {
            websiteContext += " User asked: " + question;
        }
        return supportService.getResponse(websiteContext);

    }

}
