package co.istad.streamsavro.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${topic.electronic-orders-topic}")
    private String electronicOrdersTopic;

    @Bean
    public NewTopic airlineOutputTopic() {
        return TopicBuilder
                .name("AirlineOutputTopic")
                .partitions(3)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic airlineInputTopic() {
        return TopicBuilder
                .name("AirlineInputTopic")
                .partitions(3)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic report1RequestTopic() {
        return TopicBuilder
                .name("report1-request")
                .partitions(3)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic electronicOrdersTopic() {
        return TopicBuilder
                .name(electronicOrdersTopic)
                .partitions(3)
                .replicas(3)
                .compact()
                .build();
    }

}
