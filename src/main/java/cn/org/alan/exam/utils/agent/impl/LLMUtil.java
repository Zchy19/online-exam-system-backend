package cn.org.alan.exam.utils.agent.impl;

import cn.org.alan.exam.utils.agent.AIChat;
import cn.org.alan.exam.utils.agent.Assistant;
import cn.org.alan.exam.utils.agent.Constants;





import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;




import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@Service
@ConditionalOnProperty(name = "online-exam.chat-platform.type", havingValue = "llm")
public class LLMUtil implements AIChat {

    @Value("${llm.api-key}")
    private String llmApiKey;

    @Value("${llm.base-url}")
    private String llmBaseUrl;

    @Value("${llm.model}")
    private String llmModelName;

    @Value("${embedding.api-key}")
    private String embeddingApiKey;

    @Value("${embedding.base-url}")
    private String embeddingBaseUrl;

    @Value("${embedding.model}")
    private String embeddingModelName;

    @Value("${milvus.host}")
    private String milvusHost;

    @Value("${milvus.port}")
    private Integer milvusPort;

    @Value("${milvus.username}")
    private String milvusUsername;

    @Value("${milvus.password}")
    private String milvusPassword;

    @Override
    
    public String getChatResponse(String msg) {
        
        Assistant assistant = createAssistant();

        
        ChatMessage systemMessage = new SystemMessage(Constants.systemMessage);

        
        ChatMessage userMessage = new UserMessage(msg);

        
        String input = systemMessage.text() + "\n" + userMessage.text();
        
        return assistant.answer(input);
    }

    
    private Assistant createAssistant() {

        
        OpenAiChatModel llm = OpenAiChatModel.builder().apiKey(llmApiKey) 
                .modelName(llmModelName) 
                .baseUrl(llmBaseUrl) 
                .temperature(Constants.temperature) 
                .maxTokens(Constants.maxToken) 
                .build();

        
        

        
        return AiServices.builder(Assistant.class).chatLanguageModel(llm) 


                .build();
    }

    
    public Path toPath(String relativePath) {
        try {
            
            URL fileUrl = LLMUtil.class.getClassLoader().getResource(relativePath);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            
            throw new RuntimeException(e);
        }
    }

    
    
}