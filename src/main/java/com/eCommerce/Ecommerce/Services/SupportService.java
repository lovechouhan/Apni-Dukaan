package com.eCommerce.Ecommerce.Services;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

@Service
public class SupportService {

    private ChatClient chatClient;


    @Autowired
    private ChatModel chatModel;
    
    public SupportService(ChatClient.Builder builder) {
     
        this.chatClient = builder.build();
  
    }


    public Flux<String> getResponse(String question) {
       StringBuilder prompt = new StringBuilder();
        prompt.append("You are Apni Dukan's helpful shopping consultant.\n");
        prompt.append("- Be concise and friendly.\n");
        prompt.append("- Answer product questions, order queries, returns, and general help.\n");
        prompt.append("- If you need account-specific info that you don't have, say so and guide the user.\n\n");
        if (question != null && question.trim().length() > 0) {
            
            prompt.append("user: ").append(question).append("\n");
            prompt.append("assistant: ");
            // Here you would typically call your AI model to get a response
        }

        Flux<String> response = chatModel.stream(prompt.toString());
        return response;
    }
}
