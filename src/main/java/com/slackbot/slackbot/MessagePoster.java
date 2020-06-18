package com.slackbot.slackbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.naming.spi.ObjectFactory;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class MessagePoster {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Logger logger = LoggerFactory.getLogger(MessagePoster.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private static String bearerToken;

    public static void setBearerToken(String bearerToken){
        MessagePoster.bearerToken=bearerToken;
    }

    public static void send(String msg,String userId) {
        for(int i=0; i<5;i++){
            try {
                Map <String ,String> mp = new HashMap<>();
                mp.put("text",msg);
                mp.put("channel",userId);
                logger.info(mapper.writeValueAsString(mp));
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("https://slack.com/api/chat.postMessage"))
                        .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(mp)))
                        .setHeader("Content-type","application/json")
                        .setHeader("Authorization","Bearer "+bearerToken)
                        .build();
                HttpResponse <String> response = httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );
                logger.info("Message sent!"+ response.body());
                if(response.statusCode()==200) break;
            }catch(IOException | InterruptedException | URISyntaxException e){
                e.getStackTrace();
            }
        }
    }
}
