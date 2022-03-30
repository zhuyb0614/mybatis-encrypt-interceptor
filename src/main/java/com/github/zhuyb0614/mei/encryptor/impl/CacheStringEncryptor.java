package com.github.zhuyb0614.mei.encryptor.impl;

import com.github.zhuyb0614.mei.MeiProperties;
import com.github.zhuyb0614.mei.encryptor.StringEncryptor;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
public class CacheStringEncryptor implements StringEncryptor {
    private Cache<String, String> ENCRYPT_CACHE;
    private Cache<String, String> DECRYPT_CACHE;
    private StringEncryptor stringEncryptor;

    public CacheStringEncryptor(MeiProperties meiProperties, StringEncryptor stringEncryptor) {
        this.stringEncryptor = stringEncryptor;
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
    public String encryptString(String str) {
        String cipherText = ENCRYPT_CACHE.getIfPresent(str);
        if (cipherText == null) {
            cipherText = stringEncryptor.encryptString(str);
            ENCRYPT_CACHE.put(str, cipherText);
        }
        return cipherText;
    }

    @Override
    public String decryptString(String str) {
        String sourceTxt = DECRYPT_CACHE.getIfPresent(str);
        if (sourceTxt == null) {
            sourceTxt = stringEncryptor.decryptString(str);
            ENCRYPT_CACHE.put(str, sourceTxt);
        }
        return sourceTxt;
    }

    @Override
    public Map<String, String> encryptStrings(List<String> strings) {
        return stringEncryptor.encryptStrings(strings);
    }

    @Override
    public Map<String, String> decryptStrings(List<String> strings) {
        return stringEncryptor.decryptStrings(strings);
    }

}
