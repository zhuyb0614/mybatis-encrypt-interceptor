package com.github.zhuyb0614.mei.encryptor.impl;

import com.github.zhuyb0614.mei.encryptor.IStringEncryptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReverseStringEncryptor implements IStringEncryptor {


    @Override
    public String encryptString(String str) {
        return reverseString(str);
    }

    @Override
    public String decryptString(String str) {
        return reverseString(str);
    }

    @Override
    public Map<String, String> encryptStrings(List<String> strings) {
        return batchReverseString(strings);
    }

    @Override
    public Map<String, String> decryptStrings(List<String> strings) {
        return batchReverseString(strings);
    }

    private Map<String, String> batchReverseString(List<String> strings) {
        Map<String, String> stringReverseStringMap = new HashMap<>(strings.size());
        for (String s : strings) {
            stringReverseStringMap.put(s, reverseString(s));
        }
        return stringReverseStringMap;
    }


    private String reverseString(String str) {
        return new StringBuffer(str).reverse().toString();
    }
}
