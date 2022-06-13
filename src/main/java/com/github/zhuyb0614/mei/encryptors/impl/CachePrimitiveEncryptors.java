package com.github.zhuyb0614.mei.encryptors.impl;

import com.github.zhuyb0614.mei.MeiProperties;
import com.github.zhuyb0614.mei.encryptors.PrimitiveEncryptors;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 使用GuavaCache的基本类型加密包装
 *
 * @author zhuyunbo
 */
@Slf4j
public class CachePrimitiveEncryptors implements PrimitiveEncryptors {
    private static Cache<Object, String> ENCRYPT_CACHE;
    private static Cache<String, Object> DECRYPT_CACHE;
    private PrimitiveEncryptors primitiveEncryptors;

    public CachePrimitiveEncryptors(MeiProperties meiProperties, PrimitiveEncryptors primitiveEncryptors) {
        this.primitiveEncryptors = primitiveEncryptors;
        ENCRYPT_CACHE = CacheBuilder.newBuilder()
                .expireAfterWrite(meiProperties.getCacheExpireAfterWriteSeconds(), TimeUnit.SECONDS)
                .maximumSize(meiProperties.getCacheMaximumSize())
                .build();
        DECRYPT_CACHE = CacheBuilder.newBuilder()
                .expireAfterWrite(meiProperties.getCacheExpireAfterWriteSeconds(), TimeUnit.SECONDS)
                .maximumSize(meiProperties.getCacheMaximumSize())
                .build();
    }

    @Override
    public String encryptString(Object str) {
        String cipherText = ENCRYPT_CACHE.getIfPresent(str);
        if (cipherText == null) {
            cipherText = primitiveEncryptors.encryptString(str);
            ENCRYPT_CACHE.put(str, cipherText);
        }
        return cipherText;
    }

    @Override
    public Object decryptString(String str) {
        Object sourceTxt = DECRYPT_CACHE.getIfPresent(str);
        if (sourceTxt == null) {
            sourceTxt = primitiveEncryptors.decryptString(str);
            DECRYPT_CACHE.put(str, sourceTxt);
        }
        return sourceTxt;
    }

    @Override
    public Map<Object, String> encryptStrings(List<Object> strings) {
        return primitiveEncryptors.encryptStrings(strings);
    }

    @Override
    public Map<String, Object> decryptStrings(List<String> strings) {
        return primitiveEncryptors.decryptStrings(strings);
    }

}
