package co.istad.streamsavro.topology;

import co.istad.streamsavro.avro.ElectronicOrder;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ElectronicOrderTopology {

    private final SpecificAvroSerde<ElectronicOrder> electronicOrderSerde;
    @Value("${topic.electronic-orders-topic}")
    private String electronicOrdersTopic;

    @Bean
    public Topology buildElectronicOrderTopology(StreamsBuilder streamsBuilder) {

        KStream<String, ElectronicOrder> electronicOrders = streamsBuilder
                .stream(
                        electronicOrdersTopic,
                        Consumed.with(Serdes.String(), electronicOrderSerde)
                );

        Duration duration = Duration.ofSeconds(15);
        TimeWindows tumblingWindows = TimeWindows.ofSizeWithNoGrace(duration);

        KStream<String, Long> electronicOrdersCount = electronicOrders
                .groupByKey()
                .windowedBy(tumblingWindows)
                .count()
                .toStream()
                .map((key, value) -> KeyValue.pair(key.key(), value));

        electronicOrdersCount
                .print(Printed.<String, Long>toSysOut().withLabel(electronicOrdersTopic));

        return streamsBuilder.build();
    }

}
