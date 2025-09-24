package io.github.rivon.mosu.notify.sender;

import lombok.Data;

/**
 * 短信通用返回
 *
 * @author allen
 */
@Data
public class SmsResult {
    private boolean successful;
    private Object result;
}
