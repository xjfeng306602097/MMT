package com.makro.mall.pulsar.producer;

import com.makro.mall.pulsar.annotation.PulsarProducer;
import com.makro.mall.pulsar.constant.Serialization;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.HashMap;
import java.util.Map;

@PulsarProducer
public class ProducerFactory implements PulsarProducerFactory {

    private final Map<String, ImmutablePair<Class<?>, Serialization>> topics = new HashMap<>();

    public ProducerFactory addProducer(String topic) {
        return addProducer(topic, byte[].class, Serialization.BYTE);
    }

    public ProducerFactory addProducer(String topic, Class<?> clazz) {
        topics.put(topic, new ImmutablePair<>(clazz, Serialization.JSON));
        return this;
    }

    public ProducerFactory addProducer(String topic, Class<?> clazz, Serialization serialization) {
        topics.put(topic, new ImmutablePair<>(clazz, serialization));
        return this;
    }

    @Override
    public Map<String, ImmutablePair<Class<?>, Serialization>> getTopics() {
        return topics;
    }
}
