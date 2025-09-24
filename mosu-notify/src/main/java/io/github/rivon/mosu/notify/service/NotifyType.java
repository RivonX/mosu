package io.github.rivon.mosu.notify.service;

import lombok.Getter;

/**
 * 通知类型 枚举
 *
 * @author allen
 */
@Getter
public enum NotifyType {
    REFUND("refund");

    private final String type;

    NotifyType(String type) {
        this.type = type;
    }

}
