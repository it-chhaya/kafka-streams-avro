package co.istad.streamsavro.test;

import co.istad.streamsavro.avro.ElectronicOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaDataGenerator {

    private final List<String> products = Arrays.asList(
            "PRO-001", "PRO-002", "PRO-003", "PRO-004", "PRO-005"
    );

    private final List<String> users = Arrays.asList(
            "dara", "tola", "vicheka", "seyha", "kanha"
    );

    private Random random = new Random();

    @Value("${topic.electronic-orders-topic}")
    private String electronicOrdersTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    //@Scheduled(fixedRate = 1000)
    public void generateElectronicOrders() {
        ElectronicOrder electronicOrder = new ElectronicOrder();
        electronicOrder.setOrderId(UUID.randomUUID().toString());
        electronicOrder.setElectronicId(products.get(random.nextInt(products.size())));
        electronicOrder.setPrice(random.nextDouble(999.99));
        electronicOrder.setUserId(users.get(random.nextInt(users.size())));
        electronicOrder.setTime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        kafkaTemplate.send(electronicOrdersTopic, electronicOrder.getElectronicId(), electronicOrder);
        log.info("Electronic orders generated: {}", electronicOrder.getElectronicId());
    }

}
