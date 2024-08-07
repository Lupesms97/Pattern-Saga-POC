package com.app.pocsaga.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class MongoDb {

        @Value("${spring.data.mongodb.uri}")
        private String uri;

        @Bean
        public MongoDatabaseFactory mongoConfigure(){
            return new SimpleMongoClientDatabaseFactory(uri);
        }

        @Bean
        public MongoTemplate mongoTemplate(){
            return new MongoTemplate(mongoConfigure());
        }

}
