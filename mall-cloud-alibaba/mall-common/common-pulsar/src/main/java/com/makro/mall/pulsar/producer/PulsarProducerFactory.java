package com.makro.mall.pulsar.producer;

import com.makro.mall.pulsar.constant.Serialization;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Map;

public interface PulsarProducerFactory {
    Map<String, ImmutablePair<Class<?>, Serialization>> getTopics();
}
