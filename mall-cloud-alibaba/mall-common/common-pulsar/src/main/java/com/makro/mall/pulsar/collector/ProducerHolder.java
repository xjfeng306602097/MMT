package com.makro.mall.pulsar.collector;


import com.makro.mall.pulsar.constant.Serialization;

public class ProducerHolder {

    private final String topic;
    private final Class<?> clazz;
    private final Serialization serialization;

    public ProducerHolder(String topic, Class<?> clazz, Serialization serialization) {
        this.topic = topic;
        this.clazz = clazz;
        this.serialization = serialization;
    }

    public String getTopic() {
        return topic;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Serialization getSerialization() {
        return serialization;
    }
}
