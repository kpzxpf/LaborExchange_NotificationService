package com.vlz.laborexchange_notificationservice.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value("${spring.kafka.topics.new-topic}")
    private String newApplicationTopicName;
    @Value("${spring.kafka.topics.rejected-topic}")
    private String rejectedTopicName;
    @Value("${spring.kafka.topics.withdrawn-topic}")
    private String withdrawTopicName;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic newApplicationTopic() {
        return new NewTopic(newApplicationTopicName, 1, (short) 1);
    }

    @Bean
    public NewTopic rejectedTopic() {
        return new NewTopic(rejectedTopicName, 1, (short) 1);
    }

    @Bean
    public NewTopic withdrawTopic() {
        return new NewTopic(withdrawTopicName, 1, (short) 1);
    }
}