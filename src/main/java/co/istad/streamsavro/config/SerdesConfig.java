package co.istad.streamsavro.config;

import co.istad.streamsavro.avro.DailyReport;
import co.istad.streamsavro.avro.ElectronicOrder;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class SerdesConfig {

    @Value("${spring.kafka.properties.schema.registry.url}")
    private String schemaRegistryUrl;

    private <T extends SpecificRecord> SpecificAvroSerde<T> createSerdes(Class<T> clazz) {

        Map<String, String> serdeConfig = new HashMap<>();
        serdeConfig.put("schema.registry.url", schemaRegistryUrl);

        SpecificAvroSerde<T> serde = new SpecificAvroSerde<T>();
        serde.configure(serdeConfig, false);

        return serde;
    }

    @Bean
    public SpecificAvroSerde<ElectronicOrder> electronicOrderSerdes() {
        return createSerdes(ElectronicOrder.class);
    }

    @Bean
    public SpecificAvroSerde<DailyReport> dailyReportSerdes() {
        return createSerdes(DailyReport.class);
    }

}
