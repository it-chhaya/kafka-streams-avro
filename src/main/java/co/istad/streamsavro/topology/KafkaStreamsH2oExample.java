package co.istad.streamsavro.topology;

import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.exception.PredictException;
import hex.genmodel.easy.prediction.BinomialModelPrediction;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class KafkaStreamsH2oExample {

    private static final String modelClassName = "co.istad.streamsavro.test.gbm_pojo_test";
    private static String airlineDelayPrediction = "unknown";

    private final EasyPredictModelWrapper modelWrapper;

    public KafkaStreamsH2oExample() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        hex.genmodel.GenModel rawModel;
        rawModel = (hex.genmodel.GenModel) Class.forName(modelClassName).newInstance();
        modelWrapper = new EasyPredictModelWrapper(rawModel);
    }

    @Bean
    public Topology buildH2oTopology(StreamsBuilder streamsBuilder) {

        KStream<String, String> airlineInputLines = streamsBuilder.stream(
                "AirlineInputTopic",
                Consumed.with(Serdes.String(), Serdes.String())
        );

        airlineInputLines.foreach((key, value) -> {
            if (value != null && !value.isEmpty()) {
                System.out.println("###################################");
                System.out.println("Flight Input: " + value);

                String[] values = value.split(",");
                RowData rowData = new RowData();
                rowData.put("Year", values[0]);
                rowData.put("Month", values[1]);
                rowData.put("DayofMonth ", values[2]);
                rowData.put("DayOfWeek", values[3]);
                rowData.put("CRSDepTime", values[5]);
                rowData.put("UniqueCarrier", values[8]);
                rowData.put("Origin", values[16]);
                rowData.put("Dest", values[17]);

                BinomialModelPrediction p = null;
                try {
                    p = modelWrapper.predictBinomial(rowData);
                } catch (PredictException e) {
                    System.out.println("Error prediction: " + e.getMessage());
                }

                airlineDelayPrediction = p.label;

                System.out.println("Label (aka prediction) is flight departure delayed: " + p.label);
                System.out.println("Class probabilities: ");
                for (int i = 0; i < p.classProbabilities.length; i++) {
                    if (i > 0) {
                        System.out.println(",");
                    }
                    System.out.println(p.classProbabilities[i]);
                }
                System.out.println();
                System.out.println("###################################");
            }
        });

        KStream<String, String> transformedMessage = airlineInputLines.mapValues(
                value -> "Prediction: Is Airline delayed? => " + airlineDelayPrediction
        );

        transformedMessage.to(
                "AirlineOutputTopic",
                    Produced.with(Serdes.String(), Serdes.String())
                );

        return streamsBuilder.build();
    }

}
