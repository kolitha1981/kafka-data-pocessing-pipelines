package com.productmanagement.ingress.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;

@Configuration
public class KafakaProducerConfig {

	@Value(value = "${kafka.bootstrap.address.value}")
	private String bootstrapAddress;
	@Value(value = "${kafka.schema.registry.url}")
	private String schemaRegistryUrl;

	@Bean
	public ProducerFactory<Long, GenericRecord> producerFactory() {
		/*final Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		configProps.put(ProducerConfig.CLIENT_ID_CONFIG, "AvroProductProducer");
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
		configProps.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
				schemaRegistryUrl);
		configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
		configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		return new DefaultKafkaProducerFactory<>(configProps);*/
		final Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
		configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
		configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		configProps.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
				schemaRegistryUrl);
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	public KafkaTemplate<Long, GenericRecord> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

}
