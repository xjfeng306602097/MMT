package com.makro.mall.pulsar.producer;

import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.TypedMessageBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
public class PulsarTemplate<T> {

    private final ProducerCollector producerCollector;

    public PulsarTemplate(ProducerCollector producerCollector) {
        this.producerCollector = producerCollector;
    }

    public MessageId send(String topic, T msg) throws PulsarClientException {
        //noinspection unchecked
        return producerCollector.getProducer(topic).send(msg);
    }

    public CompletableFuture<MessageId> sendAsync(String topic, T message) {
        return producerCollector.getProducer(topic).sendAsync(message);
    }

    public TypedMessageBuilder<T> createMessage(String topic, T message) {
        return producerCollector.getProducer(topic).newMessage().value(message);
    }

    public MessageId deliverAfterSec(String topic, T message, Long delay) throws PulsarClientException {
        return producerCollector.getProducer(topic).newMessage().deliverAfter(delay, TimeUnit.SECONDS).value(message).send();
        // return createMessage(topic, message).deliverAfter(delay, TimeUnit.SECONDS).send();
    }

    public CompletableFuture<MessageId> deliverAfterSecAsync(String topic, T message, Long delay) {
        return producerCollector.getProducer(topic).newMessage().deliverAfter(delay, TimeUnit.SECONDS).value(message).sendAsync();
        // return createMessage(topic, message).deliverAfter(delay, TimeUnit.SECONDS).sendAsync();
    }

}
