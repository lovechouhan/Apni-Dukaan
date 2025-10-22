package com.eCommerce.Ecommerce.Services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eCommerce.Ecommerce.Entities.ChatEntity;

import reactor.core.publisher.Flux;

@Service
public class ChatService {
    
    private ChatClient chatClient;


    @Autowired
    private ChatModel chatModel;
    

    public ChatService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
  
    }

    public ChatEntity getChatResponse(String userMessage) {
         Prompt prompt1 = new Prompt(userMessage);
         ChatEntity response = chatClient.prompt(prompt1)
                                         .call()
                                         .entity(ChatEntity.class);
         return response;
    }

    public Flux<String> getChatResponses2(String inputText) {
         Flux<String> response = chatModel.stream(inputText);
         return response;
    }
}
