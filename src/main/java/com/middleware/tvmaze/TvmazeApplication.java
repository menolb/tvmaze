package com.middleware.tvmaze;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class TvmazeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TvmazeApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // CONFIURACIÓN HARDCODED PARA EVITAR EL TIMEOUT DE LOCALHOST
    @Bean
    public MongoClient mongoClient() {
        String connectionString = "mongodb+srv://menolb13_db_user:pMymf0HpsIHWQSvn@cluster0.oq7qfji.mongodb.net/tvmaze_middleware?retryWrites=true&w=majority";
        return MongoClients.create(connectionString);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "tvmaze_middleware");
    }
}
