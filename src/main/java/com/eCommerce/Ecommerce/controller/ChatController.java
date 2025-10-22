package com.eCommerce.Ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eCommerce.Ecommerce.Services.ChatService;
import org.springframework.ui.Model;
import reactor.core.publisher.Flux;


@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }


   @PostMapping("/stream")
    public Flux<String> streamResponse(
        @RequestParam(value = "inputText") String inputText,Model model
    ) {

       // 1️ Define website context
    String websiteContext = "This is my website about fashion e-commerce, selling sarees, jewelry, and accessories. " +
                            "The website has categories: Men's wear, Women's wear, Kids' wear, Sarees, Tops, Dresses, Accessories. Users can browse, filter, and buy products.";

    // 2️ Combine context with user message
    String aiInput = websiteContext + "\nUser asked: " + inputText;

    // 3️ Pass this combined input to your AI service
    return chatService.getChatResponses2(aiInput);

    }



}