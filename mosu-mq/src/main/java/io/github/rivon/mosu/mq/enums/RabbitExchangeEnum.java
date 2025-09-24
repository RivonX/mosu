package io.github.rivon.mosu.mq.enums;

/**
 * RabbitMQ 交换机类型枚举
 *
 * @author allen
 */
public enum RabbitExchangeEnum {

    DIRECT, // 直连交换机
    FANOUT, // 扇形交换机
    TOPIC, // 主题交换机
    HEADERS; // 头交换机
}