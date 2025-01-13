package co.istad.streamsavro;

import co.istad.streamsavro.avro.ElectronicOrder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaStreamsAvroApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaStreamsAvroApplication.class, args);

        ElectronicOrder order = new ElectronicOrder();

    }

}
