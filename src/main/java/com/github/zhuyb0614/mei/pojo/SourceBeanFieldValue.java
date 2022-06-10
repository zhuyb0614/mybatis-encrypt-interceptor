package com.github.zhuyb0614.mei.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * @author yunbo.zhu
 * @version 1.0
 * @date 2022/6/8 7:17 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class SourceBeanFieldValue {
    private Object sourceBean;
    private Field field;
    private Object value;

    public void resetValue() throws IllegalAccessException {
        try {
            field.set(sourceBean, value);
        } catch (Exception e) {
            log.error("wrong data {}", this, e);
            throw e;
        }
    }
}
