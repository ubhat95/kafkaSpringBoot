package com.uttam.kafka.service;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.uttam.kafka.commons.KafkaObjectSerializer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaProducerService {
	
	protected Producer<String, Object> producer;
	
	@Autowired
	KafkaTemplate<String, Object> kafkaTemplate;
	
	@PostConstruct
	private void init() {
		log.info("intialising producer with default properties");
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", "localhost:9092");
		properties.setProperty("key.serializer", StringSerializer.class.getName());
	    properties.setProperty("value.serializer", KafkaObjectSerializer.class.getName());
	    producer = new KafkaProducer<>(properties);
	} 
	
	public boolean sendWithKafkaTemplate(String topic, String key, Object msg) {
		try{
			kafkaTemplate.send(topic,key, msg);
			return true;
		} catch (Exception e) {
			 e.printStackTrace();
			return false;		
		}
	}
	
	public boolean sendWithKafkaProducer(String topic, String key, Object msg) {
	
			ProducerRecord<String, Object> record = new ProducerRecord<>(topic,key, msg);
			producer.send(record, new Callback() {
				
				@Override
				public void onCompletion(RecordMetadata metadata, Exception e) {
					// executed when record is successfully sent or exception is thrown
					if(e==null) {
						//log.info("Topic : {} , Partition: {}, Offset: {}, TimeStamp: {}",
						//		metadata.topic(), metadata.partition(), metadata.offset(), metadata.timestamp());
					}
				}
				});
			return true;
			
	}
	
	@PreDestroy
	void close() {
		if(producer!=null)producer.close();
	}
}
