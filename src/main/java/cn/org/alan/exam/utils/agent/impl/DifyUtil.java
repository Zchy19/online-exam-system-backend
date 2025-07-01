package cn.org.alan.exam.utils.agent.impl;

import cn.hutool.json.JSONObject;
import cn.org.alan.exam.utils.agent.AIChat;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;


@Service
@ConditionalOnProperty(name = "online-exam.chat-platform.type", havingValue = "dify")
public class DifyUtil implements AIChat {
    @Value("${dify.api-key}")
    private String apiKey;

    @Value("${dify.base-url}")
    private String baseUrl;

    @Override
    public String getChatResponse(String msg) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(baseUrl);

            
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + apiKey);

            
            JSONObject requestBody = new JSONObject();
            requestBody.put("query", msg);
            requestBody.put("response_mode", "blocking");
            requestBody.put("user", "user"); 

            
            JSONObject inputs = new JSONObject();
            inputs.put("text", "Your input text"); 
            requestBody.put("inputs", inputs);

            


            StringEntity entity = new StringEntity(requestBody.toString());
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseString = EntityUtils.toString(response.getEntity());

                if (statusCode == 200) {
                    return new JSONObject(responseString).getStr("answer");
                } else {
                    throw new RuntimeException("HTTP " + statusCode + " Error: " + responseString);
                }
            }
        }
    }
}