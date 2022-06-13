package com.github.zhuyb0614.mei.encryptors.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.zhuyb0614.mei.encryptors.PrimitiveEncryptors;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将字符串翻转的基本类型加密器
 *
 * @author zhuyunbo
 */
public class ReversePrimitiveEncryptors implements PrimitiveEncryptors {
    private final static ParserConfig DEFAULT_PARSER_CONFIG = new ParserConfig();

    static {
        DEFAULT_PARSER_CONFIG.setAutoTypeSupport(true);
    }

    @Override
    public String encryptString(Object obj) {
        String sourceJson = JSON.toJSONString(obj, SerializerFeature.WriteClassName);
        return reverseString(sourceJson);
    }

    @Override
    public Object decryptString(String str) {
        Object o = JSON.parseObject(reverseString(str), Object.class, DEFAULT_PARSER_CONFIG);
        return o;
    }

    @Override
    public Map<Object, String> encryptStrings(List<Object> objects) {
        if (CollectionUtils.isEmpty(objects)) {
            return Collections.emptyMap();
        }
        Map<Object, String> objectStringMap = Maps.newHashMapWithExpectedSize(objects.size());
        for (Object object : objects) {
            objectStringMap.put(object, encryptString(object));
        }
        return objectStringMap;
    }

    @Override
    public Map<String, Object> decryptStrings(List<String> strings) {
        Map<String, Object> stringObjectMap = new HashMap<>(strings.size());
        for (String str : strings) {
            stringObjectMap.put(str, decryptString(str));
        }
        return stringObjectMap;
    }


    private String reverseString(String str) {
        return new StringBuffer(str).reverse().toString();
    }
}
