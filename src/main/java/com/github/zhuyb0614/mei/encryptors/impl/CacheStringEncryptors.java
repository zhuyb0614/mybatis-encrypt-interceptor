package com.github.zhuyb0614.mei.encryptors.impl;

import com.github.zhuyb0614.mei.MeiProperties;
import com.github.zhuyb0614.mei.encryptors.StringEncryptors;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
public class CacheStringEncryptors implements StringEncryptors {
    private Cache<Object, String> ENCRYPT_CACHE;
    private Cache<String, Object> DECRYPT_CACHE;
    private StringEncryptors stringEncryptors;

    public CacheStringEncryptors(MeiProperties meiProperties, StringEncryptors stringEncryptors) {
        this.stringEncryptors = stringEncryptors;
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
            cipherText = stringEncryptors.encryptString(str);
            ENCRYPT_CACHE.put(str, cipherText);
        }
        return cipherText;
    }

    @Override
    public Object decryptString(String str) {
        Object sourceTxt = DECRYPT_CACHE.getIfPresent(str);
        if (sourceTxt == null) {
            sourceTxt = stringEncryptors.decryptString(str);
            DECRYPT_CACHE.put(str, sourceTxt);
        }
        return sourceTxt;
    }

    @Override
    public Map<Object, String> encryptStrings(List<Object> strings) {
        return stringEncryptors.encryptStrings(strings);
    }

    @Override
    public Map<String, Object> decryptStrings(List<String> strings) {
        return stringEncryptors.decryptStrings(strings);
    }

}
