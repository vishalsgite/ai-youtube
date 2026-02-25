package com.vishal.aiyoutube.youtube_processing_service.config;

import com.vishal.aiyoutube.youtube_processing_service.dto.TopicSubmittedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, TopicSubmittedEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

        // FIXED: Using variable instead of hardcoded "localhost"
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "youtube-processing-group-v2");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Create and configure the JsonDeserializer
        JsonDeserializer<TopicSubmittedEvent> jsonDeserializer = new JsonDeserializer<>(TopicSubmittedEvent.class);
        jsonDeserializer.addTrustedPackages("com.vishal.aiyoutube.*");

        // CONFIGURE TYPE MAPPING: Handles DTO package differences across services
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("com.vishal.aiyoutube.topic_management_service.dto.TopicSubmittedEvent", TopicSubmittedEvent.class);
        typeMapper.setIdClassMapping(idClassMapping);
        jsonDeserializer.setTypeMapper(typeMapper);

        // Wrap in ErrorHandlingDeserializer to prevent "Poison Pill" loops
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jsonDeserializer)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TopicSubmittedEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TopicSubmittedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}