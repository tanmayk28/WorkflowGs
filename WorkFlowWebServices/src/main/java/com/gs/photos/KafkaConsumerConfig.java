package com.gs.photos;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import com.gs.photos.serializers.ComponentEventDeserializer;
import com.gs.photos.serializers.ImportEventSerializer;
import com.workflow.model.events.ComponentEvent;
import com.workflow.model.events.ImportEvent;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public Map<String, Object> consumerConfigs(
        @Value("${bootstrap.servers}") String bootstrapServer,
        @Value("${group.id}") String groupId
    ) {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ComponentEventDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_PLAINTEXT.name());
        config.put("sasl.kerberos.service.name", "kafka");
        return config;
    }

    @Bean
    public ConsumerFactory<String, ComponentEvent> consumerFactory(
        @Value("${bootstrap.servers}") String bootstrapServer,
        @Value("${group.id}") String groupId
    ) {
        return new DefaultKafkaConsumerFactory<>(this.consumerConfigs(bootstrapServer, groupId));
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, ComponentEvent>> kafkaListenerContainerFactory(
        @Value("${bootstrap.servers}") String bootstrapServer,
        @Value("${group.id}") String groupId
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ComponentEvent> factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(this.consumerFactory(bootstrapServer, groupId));
        return factory;

    }

    @Bean
    public ProducerFactory<String, ImportEvent> producerFactory(
        @Value("${bootstrap.servers}") String bootstrapServer,
        @Value("${group.id}") String groupId
    ) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ImportEventSerializer.class);
        configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_PLAINTEXT.name());
        configProps.put("sasl.kerberos.service.name", "kafka");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, ImportEvent> kafkaTemplate(
        @Value("${bootstrap.servers}") String bootstrapServer,
        @Value("${group.id}") String groupId

    ) {
        return new KafkaTemplate<>(this.producerFactory(bootstrapServer, groupId));
    }
}