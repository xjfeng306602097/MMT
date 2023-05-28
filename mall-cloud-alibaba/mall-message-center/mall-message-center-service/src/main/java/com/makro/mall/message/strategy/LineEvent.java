package com.makro.mall.message.strategy;

/**
 * @author jincheng
 */
public interface LineEvent<T> {

    LineEvent<T> handle(T event);

}
