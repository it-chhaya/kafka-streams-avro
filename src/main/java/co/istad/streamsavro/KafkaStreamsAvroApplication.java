package co.istad.streamsavro;

import co.istad.streamsavro.avro.ElectronicOrder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableKafkaStreams
@SpringBootApplication
public class KafkaStreamsAvroApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaStreamsAvroApplication.class, args);

        ElectronicOrder order = new ElectronicOrder();

    }

}
