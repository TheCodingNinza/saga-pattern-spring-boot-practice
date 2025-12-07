package com.saurabhsameer.products.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {


    private final String productsEventsTopicName;
    private final static Integer TOPIC_REPLICATION_FACTOR = 1;
    private final static Integer TOPIC_PARTITION = 3;

    public KafkaConfig(@Value("${products.events.topic.name}") String productsEventsTopicName) {
        this.productsEventsTopicName = productsEventsTopicName;
    }

    @Bean
    KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    NewTopic createProductsEventsTopic() {
        return TopicBuilder.name(productsEventsTopicName)
                .partitions(TOPIC_PARTITION)
                .replicas(TOPIC_REPLICATION_FACTOR)
                .build();
    }
}
