package com.github.zhuyb0614.mei.exception;

/**
 * mybatis Interceptor 加解密异常
 */
public class EncryptDecryptException extends RuntimeException {

    public EncryptDecryptException() {
    }

    public EncryptDecryptException(String message) {
        super(message);
    }

}
